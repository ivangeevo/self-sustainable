package org.btwr.self_sustainable.mixin.block;

import org.btwr.self_sustainable.block.entity.VariableCampfireBE;
import org.btwr.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    @Shadow @Final protected boolean randomTicks;

    //@Inject(method = "onBlockAdded", at = @At("HEAD"))
    private void onOnblockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof VariableCampfireBE campfireBE)) return;
        int iCurrentFireLevel = state.get(FIRE_LEVEL);
        if (iCurrentFireLevel > 1 && world.random.nextFloat() <= 0.0045F) {
            for (Direction direction : Direction.values()) {
                BlockPos adjacentPos = pos.offset(direction);
                BlockState adjacentState = world.getBlockState(adjacentPos);
                Block adjacentBlock = adjacentState.getBlock();
                FluidState adjacentFluid = adjacentState.getFluidState();

                boolean isFireSource =
                        adjacentBlock instanceof AbstractFireBlock ||
                                adjacentFluid.isIn(FluidTags.LAVA) ||
                                adjacentBlock == Blocks.LAVA ||
                                adjacentBlock == Blocks.FIRE;

                if (isFireSource) {
                    // Try to light *this* campfire from that adjacent fire source
                    campfireBE.changeFireLevel(world, 1);
                    campfireBE.onFirstLit();
                    Ignitable.playLitFX(world, pos);
                    return; // already lit, no need to continue
                }
            }
        }
    }

    //@Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
    private void onHasRandomTicks(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof TorchBlock) {
            cir.setReturnValue(true);
        }
    }

    //@Inject(method = "randomTick", at = @At("HEAD"))
    private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!(state.getBlock() instanceof TorchBlock)) {
            return;
        }

        if (!world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            return;
        }
        int i = random.nextInt(3);
        if (i > 0) {
            BlockPos blockPos = pos;
            for (int j = 0; j < i; ++j) {
                if (!world.canSetBlock(blockPos = blockPos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1))) {
                    return;
                }
                BlockState blockState = world.getBlockState(blockPos);
                if (blockState.isAir()) {
                    if (!this.canLightFire(world, blockPos)) continue;
                    world.setBlockState(blockPos, AbstractFireBlock.getState(world, blockPos));
                    return;
                }
                if (!blockState.blocksMovement()) continue;
                return;
            }
        }
        else {
            for (int k = 0; k < 3; ++k) {
                BlockPos blockPos2 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                if (!world.canSetBlock(blockPos2)) {
                    return;
                }
                if (!world.isAir(blockPos2.up()) || !this.hasBurnableBlock(world, blockPos2)) continue;
                world.setBlockState(blockPos2.up(), AbstractFireBlock.getState(world, blockPos2));
            }
        }
    }

    @Unique
    private boolean canLightFire(WorldView world, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (!this.hasBurnableBlock(world, pos.offset(direction))) continue;
            return true;
        }
        return false;
    }

    @Unique
    private boolean hasBurnableBlock(WorldView world, BlockPos pos) {
        if (pos.getY() >= world.getBottomY() && pos.getY() < world.getTopY() && !world.isChunkLoaded(pos)) {
            return false;
        }
        return world.getBlockState(pos).isBurnable();
    }

}