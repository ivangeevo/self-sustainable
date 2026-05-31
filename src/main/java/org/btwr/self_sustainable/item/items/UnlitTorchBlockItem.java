package org.btwr.self_sustainable.item.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.interfaces.IgnitableTorchItem;
import org.btwr.self_sustainable.util.TorchIgnitionHelper;

public class UnlitTorchBlockItem extends VerticallyAttachableBlockItem implements IgnitableTorchItem {

    public UnlitTorchBlockItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection) {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack heldStack = context.getStack();
        Hand hand = context.getHand();

        if (player != null) {
            BlockPos firePos = findFireInSight(player, world);
            if (firePos != null) {
                if (!world.isClient) {
                    lightTorch(heldStack, world, firePos, player, hand);
                }
                return ActionResult.SUCCESS;
            }
        }

        // Direct hit on ignition source (campfire, lit block, etc.)
        if (isIgnitionSource(state)) {

            // Handle click on brick oven
            if (state.getBlock() instanceof BrickOvenBlock) {
                // Only modify interaction on the front face
                if (context.getSide() != state.get(BrickOvenBlock.FACING)) return ActionResult.PASS;

                if (!world.isClient && player != null) {
                    // if it's lit, and it's a bottom portion
                    double relativeClickY = context.getHitPos().y - pos.getY();
                    if (relativeClickY < BrickOvenBlock.CLICK_Y_BOTTOM_PORTION) {
                        lightTorch(heldStack, world, pos, player, hand);
                        return ActionResult.SUCCESS;
                        // else fail on other attempts
                    } else {
                        return ActionResult.FAIL;
                    }
                }
            }
            if (!world.isClient && player != null) {
                lightTorch(heldStack, world, pos, player, hand);
            }
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Handles right-click in air while looking at fire
        BlockPos firePos = findFireInSight(user, world);
        if (firePos != null) {
            if (!world.isClient) {
                lightTorch(stack, world, firePos, user, hand);
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }

        return super.use(world, user, hand);
    }

    @Override
    public void lightTorch(ItemStack stack, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        Item litTorch = stack.isOf(ModItems.SOUL_TORCH_UNLIT) ? Items.SOUL_TORCH : Items.TORCH;
        TorchIgnitionHelper.lightInfiniteTorch(litTorch, world, pos, player, hand, stack);
    }

}