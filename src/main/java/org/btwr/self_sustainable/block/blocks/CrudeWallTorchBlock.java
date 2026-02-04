package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import org.btwr.self_sustainable.block.entity.TorchBE;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class CrudeWallTorchBlock extends AbstractCrudeTorchBlock {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public CrudeWallTorchBlock(Settings settings, ParticleEffect particle, TorchFireState fireState) {
        super(settings, particle, fireState);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return WallTorchBlock.getBoundingShape(state);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return WallTorchBlock.canPlaceAt(world, pos, state.get(FACING));
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState torchState = Blocks.WALL_TORCH.getPlacementState(ctx);

        if (torchState != null) {
            BlockState state = this.getDefaultState();
            Direction d = torchState.get(FACING);
            return state.with(FACING, d);
        }

        return null;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction.getOpposite() == state.get(FACING) && !state.canPlaceAt(world, pos)) {
            return Blocks.AIR.getDefaultState();
        }
        return state;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void smoulder(World world, BlockPos pos, BlockState state) {
        super.smoulder(world, pos, state);

        CrudeWallTorchBlock newTorch;
        newTorch = handler.getWallTorch(TorchFireState.SMOULDER);

        world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
    }

    @Override
    public void burnOut(World world, BlockPos pos, BlockState state, boolean playSound) {
        super.burnOut(world, pos, state, playSound);

        CrudeWallTorchBlock newTorch;
        newTorch = handler.getWallTorch(TorchFireState.BURNED_OUT);

        world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
    }

    @Override
    public void light(World world, BlockPos pos, BlockState state) {
        super.light(world, pos, state);

        CrudeWallTorchBlock newTorch = handler.getWallTorch(TorchFireState.LIT);
        world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
    }

    @Override
    public boolean isWallTorch() {
        return true;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return AbstractCrudeTorchBlock.validateTicker(type, ModBlockEntities.TORCH, TorchBE::tick);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

}