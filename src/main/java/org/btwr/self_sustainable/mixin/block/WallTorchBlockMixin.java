package org.btwr.self_sustainable.mixin.block;

import org.btwr.self_sustainable.block.ModBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallTorchBlock.class)
public abstract class WallTorchBlockMixin extends AbstractTorchBlock {

    protected WallTorchBlockMixin(Settings settings) {
        super(settings);
    }

    // No particle should display for unlit torches
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void stopParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.isOf(ModBlocks.WALL_TORCH_UNLIT)) {
            ci.cancel();
        }
    }

}