package net.ivangeevo.self_sustainable.mixin.block;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.CrudeWallTorchBlock;
import net.ivangeevo.self_sustainable.block.entity.TorchBE;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends AbstractTorchBlock {

    protected TorchBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(SimpleParticleType particle, Settings settings, CallbackInfo ci) {

    }

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void stopParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.isOf(ModBlocks.TORCH_UNLIT) || state.isOf(ModBlocks.WALL_TORCH_UNLIT)) {
            ci.cancel();
        }
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        BlockState state = blockAccess.getBlockState(pos);
        return state.isOf(ModBlocks.TORCH_UNLIT) || state.isOf(ModBlocks.WALL_TORCH_UNLIT);
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos) {
        if (this.getCanBeSetOnFireDirectly(world, pos)) {

            if (!world.hasRain(pos)) {
                changeTorch(world, pos, world.getBlockState(pos));
                Ignitable.playLitFX(world, pos);
            } else {
                Ignitable.playExtinguishSound(world, pos, false);
            }

            return true;
        }

        return false;
    }

    @Unique
    public void changeTorch(World world, BlockPos pos, BlockState oldState) {
        BlockState newState;

        if (oldState.isOf(ModBlocks.WALL_TORCH_UNLIT)) {
            newState = Blocks.WALL_TORCH.getDefaultState().with(HorizontalFacingBlock.FACING, oldState.get(CrudeWallTorchBlock.FACING));
        } else {
            newState = Blocks.TORCH.getDefaultState();
        }

        world.setBlockState(pos, newState);
    }
}
