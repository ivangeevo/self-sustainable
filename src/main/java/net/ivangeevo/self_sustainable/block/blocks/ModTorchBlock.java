package net.ivangeevo.self_sustainable.block.blocks;

import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.*;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntSupplier;

public class ModTorchBlock extends AbstractTorchBlock {

    public ModTorchBlock(Settings settings, ParticleEffect particle, TorchFireState fireState, IntSupplier maxFuel) {
        super(settings, particle, fireState, maxFuel);
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Blocks.TORCH.getOutlineShape(state, world, pos, context);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return Blocks.TORCH.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos)
    {
        return Blocks.TORCH.canPlaceAt(state, world, pos);
    }

    @Override
    public boolean isWall() { return false; }


}