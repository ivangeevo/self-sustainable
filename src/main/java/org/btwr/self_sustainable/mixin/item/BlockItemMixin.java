package org.btwr.self_sustainable.mixin.item;

import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.registry.LitBlockRegistry;
import org.btwr.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item implements IgnitableBlock {

    @Shadow public abstract ActionResult place(ItemPlacementContext context);
    @Shadow public abstract ActionResult useOnBlock(ItemUsageContext context);

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    // TODO: Find a way to make this less hardcoded.
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedOnUse(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        ItemStack heldStack = context.getStack();

        if (heldStack.isIn(ModTags.Items.CAN_START_FIRE_ON_USE)) {

            // Check if the block at the position is viable for lighting up.
            if (!LitBlockRegistry.isLit(blockState)) {
                if (world.canPlayerModifyAt(player, pos)) {
                    if (!world.isClient) {
                        btwr$attemptToLightBlock(context.getStack(), world, pos, context.getSide());
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
                else {
                    cir.setReturnValue(ActionResult.FAIL);
                }
            }
            else {
                // Default logic for placing or using the item
                ActionResult actionResult = this.place(new ItemPlacementContext(context));
                if (!actionResult.isAccepted() && context.getStack().contains(DataComponentTypes.FOOD)) {
                    ActionResult actionResult2 = this.use(context.getWorld(), context.getPlayer(), context.getHand()).getResult();
                    cir.setReturnValue(actionResult2 == ActionResult.CONSUME ? ActionResult.CONSUME_PARTIAL : actionResult2);
                }
                cir.setReturnValue(actionResult);
            }
        }
    }

    @Override
    public boolean btwr$attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing) {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if (targetBlock != null && targetBlock.btwr$getCanBeSetOnFireDirectlyByItem(world, pos)) {
            return targetBlock.btwr$setOnFireDirectly(world, pos);
        }

        return false;
    }

    /**
    @Unique
    private boolean isLightableFromOnUseBlock(Block block, ItemStack stack) {
      return block == Blocks.CAMPFIRE
              || (block == ModBlocks.OVEN_BRICK && stack.getItem() != ModBlocks.OVEN_BRICK.asItem())
              || block == ModBlocks.CRUDE_TORCH_UNLIT
              || block == ModBlocks.CRUDE_WALL_TORCH_UNLIT
              || block == ModBlocks.TORCH_UNLIT
              || block == ModBlocks.WALL_TORCH_UNLIT;
    }
    **/

}