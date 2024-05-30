package net.ivangeevo.self_sustainable.block;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.interfaces.VariableCampfireBlock;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.block.CampfireBlock.*;

public class CampfireBlockManager implements Ignitable, VariableCampfireBlock
{
    public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded campfireAdded;
        campfireAdded = (CampfireBlockEntityAdded) blockEntity;

        if (blockEntity instanceof CampfireBlockEntity campfireBE)
        {
            Optional<CampfireCookingRecipe> optional;



            if (heldStack.isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS) && !state.get(LIT))
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
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);

                    return ActionResult.SUCCESS;
                }

                if (heldStack.isEmpty() && getCookStack(campfireBE).isEmpty())
                {
                    setHasSpit(world, state, pos, false);
                    player.giveItemStack(new ItemStack(Items.STICK));
                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);

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


        }
        return ActionResult.PASS;
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

    public static boolean igniteCampfire(World world, PlayerEntity player, Hand hand, BlockState state, BlockPos pos)
    {
        if (player.getStackInHand(hand).isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS) && CampfireBlock.canBeLit(state)
                && world.setBlockState(pos, state.with(CampfireBlock.LIT, true)))
        {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            ItemStack heldStack = player.getMainHandStack();
            heldStack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
            Ignitable.playLitFX(world, pos);

        }
        return false;
    }

    public static boolean setOrRemoveItems(World world, PlayerEntity player, Hand hand, BlockState state, BlockPos pos)
    {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof CampfireBlockEntity))
            return false;

        if (!getHasSpit(world, pos) && itemStack.isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS))
        {
            setHasSpit(world, state, pos, true);
            itemStack.decrement(1);
        }
        else if (getHasSpit(world, pos))
        {
            setHasSpit(world, state, pos, false);
            player.giveItemStack(new ItemStack(Items.STICK));
        }
        return true;
    }

    public static boolean getHasSpit(WorldAccess blockAccess, BlockPos pos)
    {
        return blockAccess.getBlockState(pos).get(HAS_SPIT);
    }


    public static boolean setHasSpit(World world, BlockState state, BlockPos pos, boolean bHasSpit)
    {
       return !world.isClient() && world.setBlockState(pos, state.with(HAS_SPIT, bHasSpit));

    }

}
