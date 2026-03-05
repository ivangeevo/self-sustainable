package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.btwr.self_sustainable.block.entity.HamperBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.btwr.self_sustainable.util.TickableBlockEntity;
import org.jetbrains.annotations.Nullable;

public class HamperBlock extends AbstractBasketBlock {

    public static final MapCodec<HamperBlock> CODEC = HamperBlock.createCodec(HamperBlock::new);

    private static final VoxelShape X_AXIS_CLOSED_SHAPE = Block.createCuboidShape(
            2F, 2F, 1F, 14F, 15F, 15F
    );

    private static final VoxelShape Z_AXIS_CLOSED_SHAPE = Block.createCuboidShape(
            1F, 2F, 2F, 15F, 15F, 14F
    );

    private static final VoxelShape X_AXIS_OPEN_SHAPE = Block.createCuboidShape(
            2F, 2F, 1F, 14F, 13F, 15F
    );

    private static final VoxelShape Z_AXIS_OPEN_SHAPE = Block.createCuboidShape(
            1F, 2F, 2F, 15F, 13F, 14F
    );

    public HamperBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? TickableBlockEntity.getTicker() : null;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof HamperBE be) {
                player.openHandledScreen(be);
                be.updateOpen(state);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HamperBE be) {
            be.tick();
        }
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HamperBE(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction.Axis axis = state.get(FACING).getAxis();
        VoxelShape openShape = axis == Direction.Axis.X ? X_AXIS_OPEN_SHAPE : Z_AXIS_OPEN_SHAPE;
        VoxelShape closedShape = axis == Direction.Axis.X ? X_AXIS_CLOSED_SHAPE : Z_AXIS_CLOSED_SHAPE;

        return state.get(OPEN) ? openShape : closedShape;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Use only the closed shape for collision handling to avoid clipping into the block
        Direction.Axis axis = state.get(FACING).getAxis();
        return axis == Direction.Axis.X ? X_AXIS_CLOSED_SHAPE : Z_AXIS_CLOSED_SHAPE;
    }
}