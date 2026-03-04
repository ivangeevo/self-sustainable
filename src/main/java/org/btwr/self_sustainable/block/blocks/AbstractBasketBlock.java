package org.btwr.self_sustainable.block.blocks;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.btwr.self_sustainable.block.entity.AbstractPrimitiveStorageBE;
import org.btwr.self_sustainable.network.SyncWickerBasketS2C;

/**
 * Adapted directly from Primitive Storage (CC0).
 *
 * <p>Original project:
 * <a href="https://github.com/jeffinitup/primitive-storage/">
 * https://github.com/jeffinitup/primitive-storage/
 * </a>
 *
 * <p>Original author:
 * JeffyJamzHD
 *
 */
public abstract class AbstractBasketBlock extends BlockWithEntity {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty OPEN = Properties.OPEN;
    public static final BooleanProperty CRUSHED = BooleanProperty.of("crushed");

    public AbstractBasketBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager()
                .getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(OPEN, false)
                .with(CRUSHED, false)
        );
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractPrimitiveStorageBE be) {
                be.updateOpen(state, pos, hit);
                be.markDirty();

                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                PiglinBrain.onGuardedBlockInteracted(player, true);
            }

            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, CRUSHED);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState stateBelow = world.getBlockState(pos.down());
        return !stateBelow.isOf(this) && stateBelow.isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient()) {
            world.scheduleBlockTick(pos, this, 1);
        }
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity instanceof FallingBlockEntity fallingBE) {
            float dist = fallingBE.timeFalling;
            if (dist > 10 || fallingBE.getBlockState().isIn(BlockTags.ANVIL)) {
                BlockState basketState = entity.getWorld().getBlockState(entity.getBlockPos());
                entity.getWorld().setBlockState(BlockPos.ofFloored(entity.getPos()), basketState.with(CRUSHED, true), Block.NOTIFY_ALL);
                entity.getWorld().breakBlock(entity.getBlockPos(), true, entity);
            }
        }

        super.onEntityLand(world, entity);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBlockEntity(pos) instanceof AbstractPrimitiveStorageBE be) {
            be.scheduledTick();
        }

        if (FallingBlock.canFallThrough(world.getBlockState(pos.down()))) {
            world.breakBlock(pos, true);
        }

        super.scheduledTick(state, world, pos, random);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        world.scheduleBlockTick(pos, this, 1);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    public static void sendSyncPacket(World world, BlockPos pos, DefaultedList<ItemStack> inventory) {
        if (world.isClient()) return;
        SyncWickerBasketS2C payload = new SyncWickerBasketS2C(pos, inventory);

        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
            ServerPlayNetworking.send(serverPlayer, payload);
        }
    }

}