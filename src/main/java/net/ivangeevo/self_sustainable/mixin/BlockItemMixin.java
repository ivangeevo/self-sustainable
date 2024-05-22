package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements Ignitable
{

    // Injected logic for replacing vanilla's placed block torch with the modded one, so we can utilize all block entity capabilities.
    //@Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void injectedPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        BlockPos pos = context.getBlockPos();
        BlockState torchState = Blocks.TORCH.getDefaultState();
        BlockState wallTorchState = Blocks.WALL_TORCH.getStateWithProperties(state);

        boolean setModdedTorchState = context.getWorld().setBlockState(pos, torchState,
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

        boolean setModdedWallTorchState = context.getWorld().setBlockState(pos, wallTorchState,
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);


        if (context.getStack().isOf(Items.TORCH))
        {
            cir.setReturnValue(setModdedTorchState);
        }

        if (context.getStack().isOf(Items.TORCH))
        {
            cir.setReturnValue(setModdedTorchState);
        }

    }


/**
    //@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void injectedUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);


        if ( isTorch(context.getStack()) && !state.get(LIT) )
        {
            if (!state.get(LIT))
            {
                context.getWorld().setBlockState(pos, state.with(LIT, true));
                playLitFX(context.getWorld(), pos);
            }
            else
            {
                cir.setReturnValue(ActionResult.FAIL);
            }

            cir.setReturnValue(ActionResult.SUCCESS);

        }

    }
 **/

    /**
    @Unique
    private boolean isTorch(ItemStack stack)
    {
        return ( stack.isOf(ModItems.CRUDE_TORCH) || stack.isOf(Items.TORCH) );
    }
    **/


}
