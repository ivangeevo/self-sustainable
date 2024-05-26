package net.ivangeevo.self_sustainable.block;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.interfaces.VariableCampfireBlock;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.ivangeevo.self_sustainable.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Optional;

import static net.minecraft.block.CampfireBlock.*;

public class CampfireBlockManager implements Ignitable, VariableCampfireBlock
{
    public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Use this cast bto get access to the new variables.
        CampfireBlockEntityAdded addedVars;
        addedVars = (CampfireBlockEntityAdded) blockEntity;


        if (blockEntity instanceof CampfireBlockEntity campfireBlockEntity)
        {
            Optional<CampfireCookingRecipe> optional;

            if (!getHasSpit(world, pos))
            {
                if (heldStack.isOf(Items.STICK)) // Check if the item in hand is a stick
                {
                    setHasSpit(world, pos, true);
                    heldStack.decrement(1); // Decrease the heldStack count
                    return ActionResult.SUCCESS;
                }
            }
            else
            {
                if (heldStack.isEmpty()) // Check if the player's hand is empty
                {
                    setHasSpit(world, pos, false);
                    player.giveItemStack(new ItemStack(Items.STICK));
                    return ActionResult.SUCCESS;
                }
                else if ((optional = campfireBlockEntity.getRecipeFor(heldStack)).isPresent())
                {
                    campfireBlockEntity.addItem(player, player.getAbilities().creativeMode ? heldStack.copy() : heldStack, optional.get().getCookTime());
                    addedVars.setCookStack(heldStack);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.PASS;
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

        if (!getHasSpit(state) && itemStack.isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS))
        {
            setHasSpit(world, pos, true);
            itemStack.decrement(1);
        }
        else if (getHasSpit(state))
        {
            setHasSpit(world, pos, false);
            player.giveItemStack(new ItemStack(Items.STICK));
        }
        return true;
    }

    public static boolean getHasSpit(WorldAccess blockAccess, BlockPos pos)
    {
        return getHasSpit(blockAccess.getBlockState(pos));
    }

    public static boolean getHasSpit(BlockState state)
    {
        return state.get(HAS_SPIT);
    }

    public static void setHasSpit(World world, BlockPos pos, boolean bHasSpit)
    {
        BlockState newState = setHasSpit(world.getBlockState(pos), bHasSpit);
        world.setBlockState(pos, newState);
    }

    public static BlockState setHasSpit(BlockState state, boolean bHasSpit)
    {
        return state.with(HAS_SPIT, bHasSpit);
    }
}
