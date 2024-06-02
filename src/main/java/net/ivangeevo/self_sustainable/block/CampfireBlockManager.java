package net.ivangeevo.self_sustainable.block;

import com.google.common.collect.Maps;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.interfaces.VariableCampfireBlock;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.block.CampfireBlock.*;

public class CampfireBlockManager implements Ignitable, VariableCampfireBlock
{

    public static final Map<Item, Integer> FUEL_TIME_MAP = createFuelTimeMap();

    public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded campfireAdded;
        campfireAdded = (CampfireBlockEntityAdded) blockEntity;

        if (blockEntity instanceof CampfireBlockEntity campfireBE)
        {
            Optional<CampfireCookingRecipe> optional;

            if (player.getStackInHand(hand).isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS) && !state.get(LIT))
            {
                if (!world.isClient())
                { // Only execute on the server
                    world.setBlockState(pos, state.with(LIT, true));
                    heldStack.damage(1, player, (p) -> {
                        p.sendToolBreakStatus(hand);
                    });
                }
                Ignitable.playLitFX(world, pos);

                return ActionResult.SUCCESS;
            }

            // Handle stick input
            if (!getHasSpit(world, pos))
            {
                if (heldStack.isOf(Items.STICK))
                {
                    setHasSpit(world, state, pos, true);
                    heldStack.decrement(1); // Decrease the heldStack count

                    return ActionResult.SUCCESS;
                }
            }
            else
            {
                if (!getCookStack(campfireBE).isEmpty() && !heldStack.isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS))
                {
                    campfireAdded.retrieveItem(world, campfireBE, player);
                    playGetItemSound(world, pos, player);

                    return ActionResult.SUCCESS;
                }

                if (heldStack.isEmpty() && getCookStack(campfireBE).isEmpty())
                {
                    setHasSpit(world, state, pos, false);
                    player.giveItemStack(new ItemStack(Items.STICK));
                    playGetItemSound(world, pos, player);

                    return ActionResult.SUCCESS;
                }
                else if ((optional = campfireBE.getRecipeFor(heldStack)).isPresent())
                {
                    if (getCookStack(campfireBE).isEmpty())
                    {

                        campfireBE.addItem(player,
                                player.getAbilities().creativeMode
                                        ? heldStack.copy()
                                        : heldStack, optional.get().getCookTime());

                        return ActionResult.SUCCESS;
                    }
                }
            }

            if (state.get(FIRE_LEVEL) > 0 || getFuelState(world, pos) == CampfireState.SMOULDERING)
            {
                int itemBurnTime = FUEL_TIME_MAP.get(heldStack.getItem());

                if ( heldStack.getItem().getCanBeFedDirectlyIntoCampfire(itemBurnTime) )
                {
                    if ( !world.isClient )
                    {
                        BlockPos soundPos =
                                new BlockPos(
                                        (int) (pos.getX() + 0.5D),
                                        (int) (pos.getY() + 0.5D),
                                        (int) (pos.getZ() + 0.5D));

                        world.playSound( null, soundPos,
                                SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                                0.2F + world.random.nextFloat() * 0.1F,
                                world.random.nextFloat() * 0.25F + 1.25F );

                        campfireBE.addBurnTime(state, heldStack.getItem().getCampfireBurnTime(itemBurnTime));
                    }

                    heldStack.decrement(1);

                    return ActionResult.SUCCESS;
                }
            }


        }
        return ActionResult.PASS;
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (isNonFlammableWood(registryEntry.value())) continue;
            fuelTimes.put(registryEntry.value(), fuelTime);
        }
    }
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        fuelTimes.put(item2, fuelTime);
    }

    /**
     * {@return whether the provided {@code item} is in the {@link
     * ItemTags#NON_FLAMMABLE_WOOD non_flammable_wood} tag}
     */
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        addFuel(map, Blocks.COAL_BLOCK, 14400);
        addFuel(map, Items.BLAZE_ROD, 12800);

        // Logs
        addFuel(map, Items.BIRCH_LOG, 16000);
        addFuel(map, Items.ACACIA_LOG, 16000);
        addFuel(map, Items.OAK_LOG, 12800);
        addFuel(map, Items.DARK_OAK_LOG, 12800);
        addFuel(map, Items.CHERRY_LOG, 12800);
        addFuel(map, Items.SPRUCE_LOG, 9600);
        addFuel(map, Items.MANGROVE_LOG, 8400);
        addFuel(map, Items.JUNGLE_LOG, 6400);
        addFuel(map, ItemTags.BAMBOO_BLOCKS, 500);

        // Planks
        addFuel(map, Items.BIRCH_PLANKS, 16000);
        addFuel(map, Items.ACACIA_PLANKS, 16000);
        addFuel(map, Items.OAK_PLANKS, 12800);
        addFuel(map, Items.DARK_OAK_PLANKS, 12800);
        addFuel(map, Items.CHERRY_PLANKS, 12800);
        addFuel(map, Items.SPRUCE_PLANKS, 9600);
        addFuel(map, Items.MANGROVE_PLANKS, 8400);
        addFuel(map, Items.JUNGLE_PLANKS, 6400);
        addFuel(map, Items.BAMBOO_PLANKS, 40);

        // Wooden Stairs
        addFuel(map, Items.BIRCH_STAIRS, 400);
        addFuel(map, Items.ACACIA_STAIRS, 400);
        addFuel(map, Items.OAK_STAIRS, 300);
        addFuel(map, Items.DARK_OAK_STAIRS, 300);
        addFuel(map, Items.CHERRY_STAIRS, 300);
        addFuel(map, Items.SPRUCE_STAIRS, 200);
        addFuel(map, Items.MANGROVE_STAIRS, 200);
        addFuel(map, Items.JUNGLE_STAIRS, 70);
        addFuel(map, Items.BAMBOO_STAIRS, 30);


        addFuel(map, Blocks.BAMBOO_MOSAIC, 40);
        addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 30);
        addFuel(map, ItemTags.WOODEN_SLABS, 150);
        addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 20);
        addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map, ItemTags.WOODEN_FENCES, 300);
        addFuel(map, ItemTags.FENCE_GATES, 300);
        addFuel(map, Blocks.NOTE_BLOCK, 300);
        addFuel(map, Blocks.BOOKSHELF, 300);
        addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map, Blocks.LECTERN, 300);
        addFuel(map, Blocks.JUKEBOX, 300);
        addFuel(map, Blocks.CHEST, 300);
        addFuel(map, Blocks.TRAPPED_CHEST, 300);
        addFuel(map, Blocks.CRAFTING_TABLE, 300);
        addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map, ItemTags.BANNERS, 300);
        addFuel(map, Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map, Blocks.LADDER, 300);
        addFuel(map, ItemTags.SIGNS, 200);
        addFuel(map, ItemTags.HANGING_SIGNS, 800);
        addFuel(map, Items.WOODEN_SHOVEL, 200);
        addFuel(map, Items.WOODEN_SWORD, 200);
        addFuel(map, Items.WOODEN_HOE, 200);
        addFuel(map, Items.WOODEN_AXE, 200);
        addFuel(map, Items.WOODEN_PICKAXE, 200);
        addFuel(map, ItemTags.WOODEN_DOORS, 200);
        addFuel(map, ItemTags.BOATS, 1200);
        addFuel(map, ItemTags.WOOL, 100);
        addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map, Items.STICK, 50);
        addFuel(map, ItemTags.SAPLINGS, 100);
        addFuel(map, Items.BOWL, 100);
        addFuel(map, ItemTags.WOOL_CARPETS, 67);
        addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map, Items.CROSSBOW, 300);
        addFuel(map, Blocks.BAMBOO, 50);
        addFuel(map, Blocks.DEAD_BUSH, 100);
        addFuel(map, Blocks.SCAFFOLDING, 50);
        addFuel(map, Blocks.LOOM, 300);
        addFuel(map, Blocks.BARREL, 300);
        addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map, Blocks.FLETCHING_TABLE, 300);
        addFuel(map, Blocks.SMITHING_TABLE, 300);
        addFuel(map, Blocks.COMPOSTER, 300);
        addFuel(map, Blocks.AZALEA, 100);
        addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }


    private static CampfireState getFuelState(WorldAccess blockAccess, BlockPos pos)
    {
        return getFuelState(blockAccess.getBlockState(pos));
    }

    private static CampfireState getFuelState(BlockState state)
    {
        return state.get(FUEL_STATE);
    }



    private static ItemStack getCookStack(CampfireBlockEntity campfireBE)
    {
        return campfireBE.getItemsBeingCooked().get(0);
    }


    public static VoxelShape setCustomShapes(BlockState state)
    {
        if (!state.get(HAS_SPIT))
        {
            return SHAPE;
        }
        else
        {
            return SHAPE_WITH_SPIT;
        }
    }

    public static void appendCustomProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, FUEL_STATE, HAS_SPIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    public static boolean getHasSpit(WorldAccess blockAccess, BlockPos pos)
    {
        return blockAccess.getBlockState(pos).get(HAS_SPIT);
    }


    public static boolean setHasSpit(World world, BlockState state, BlockPos pos, boolean bHasSpit)
    {
       return !world.isClient() && world.setBlockState(pos, state.with(HAS_SPIT, bHasSpit));

    }

    private static void playGetItemSound(World world, BlockPos pos, PlayerEntity player)
    {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                ( ( player.getRandom().nextFloat() - player.getRandom().nextFloat() ) * 0.7F + 1F ) * 2F);
    }

}
