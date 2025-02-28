package net.ivangeevo.self_sustainable.mixin.block;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.CrudeWallTorchBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WallTorchBlock.class)
public abstract class WallTorchBlockMixin extends AbstractTorchBlock {

    protected WallTorchBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void stopParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.isOf(ModBlocks.WALL_TORCH_UNLIT)) {
            ci.cancel();
        }
    }

}
