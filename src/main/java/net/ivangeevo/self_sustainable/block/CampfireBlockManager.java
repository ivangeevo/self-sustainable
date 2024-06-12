package net.ivangeevo.self_sustainable.block;

import net.ivangeevo.self_sustainable.block.entity.VariableCampfireBE;
import net.ivangeevo.self_sustainable.block.interfaces.VariableCampfireBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShovelItem;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.block.CampfireBlock.*;

public class CampfireBlockManager implements Ignitable, VariableCampfireBlock
{


    public static ActionResult onUse(BlockState state, @NotNull World world, BlockPos pos, @NotNull PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof VariableCampfireBE campfireBE)
        {

            if (heldStack.getItem() instanceof ShovelItem && state.get(FIRE_LEVEL) > 0)
            {
                if (!world.isClient)
                {
                    campfireBE.changeFireLevel(world, 0);
                }

                Ignitable.playExtinguishSound(world, pos, false);

                return ActionResult.SUCCESS;
            }


            Optional<CampfireCookingRecipe> optional;

            // Handle stick input
            if (!getHasSpit(world, pos))
            {
                if (heldStack.isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS) && !(state.get(FUEL_STATE) == CampfireState.BURNED_OUT))
                {
                    setHasSpit(world, state, pos, true);
                    heldStack.decrement(1); // Decrease the heldStack count

                    return ActionResult.SUCCESS;
                }
            }
            else
            {

                Map<Item, Integer> fuelMap = AbstractFurnaceBlockEntity.createFuelTimeMap();

                if (!getCookStack(campfireBE).isEmpty() && !isIgnitableItem(heldStack) && !fuelMap.containsKey(heldStack.getItem()))
                {
                    campfireBE.retrieveItem(world, campfireBE, player);
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
                int itemBurnTime = getItemFuelTime(heldStack);

                if ( heldStack.getItem().getCanBeFedDirectlyIntoCampfire(heldStack) )
                {
                    if ( !world.isClient )
                    {
                        Ignitable.playLitFX(world, pos);
                        campfireBE.addBurnTime(state, itemBurnTime);
                    }

                    heldStack.decrement(1);

                    return ActionResult.SUCCESS;
                }
            }


        }

        return ActionResult.PASS;
    }

    private static boolean isIgnitableItem(ItemStack stack)
    {
        return stack.isIn(ModTags.Items.DIRECT_IGNITERS)
                || stack.isIn(ModTags.Items.PRIMITIVE_FIRESTARTERS)
                || stack.getItem() == Items.FLINT_AND_STEEL;
    }

    public static int getItemFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    private static CampfireState getFuelState(WorldAccess blockAccess, BlockPos pos)
    {
        return getFuelState(blockAccess.getBlockState(pos));
    }

    private static CampfireState getFuelState(BlockState state)
    {
        return state.get(FUEL_STATE);
    }



    private static ItemStack getCookStack(VariableCampfireBE campfireBE)
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
        builder.add(LIT, FUEL_STATE, FIRE_LEVEL, HAS_SPIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    public static void appendCustomPropertiesNoLIT(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FUEL_STATE, FIRE_LEVEL, HAS_SPIT, SIGNAL_FIRE, WATERLOGGED, FACING);
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
