package org.btwr.self_sustainable.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlock$AbstractBlockStateMixin {

    @Shadow public abstract Block getBlock();

    // Changes the raycast shape for fire so that blocks like unlit torches can be used to interact with it
    // from all sides and not just the small voxel shape it has on the bottom
    @Inject(method = "getRaycastShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void expandFireRaycast(BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if (this.getBlock().getDefaultState().isIn(BlockTags.FIRE)) {
            cir.setReturnValue(VoxelShapes.fullCube());
        }
    }
}