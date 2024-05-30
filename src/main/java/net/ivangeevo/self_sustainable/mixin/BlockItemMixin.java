package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin implements Ignitable
{
    // keep for reference on how to change what block is placed from an item

    /**
    // Injected logic for replacing vanilla's placed block torch with the modded one, so we can utilize all block entity capabilities.
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void injectedPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        BlockPos pos = context.getBlockPos();
        BlockState torchState = Blocks.DIRT.getDefaultState();
        BlockState wallTorchState = Blocks.WALL_TORCH.getStateWithProperties(state);

        boolean setModdedTorchState = context.getWorld().setBlockState(pos, torchState,
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

        if (context.getStack().isOf(Items.CAMPFIRE))
        {
            cir.setReturnValue(setModdedTorchState);
        }



    }
    **/




}
