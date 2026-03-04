package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.entity.WickerBasketBE;
import org.btwr.self_sustainable.util.TickableBlockEntity;
import org.jetbrains.annotations.Nullable;

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
public class WickerBasketBlock extends AbstractBasketBlock {

    public static final MapCodec<WickerBasketBlock> CODEC = WickerBasketBlock.createCodec(WickerBasketBlock::new);

    public static final float BASKET_OPEN_HEIGHT = 0.75F;

    private static final double LID_OPEN_LIP_HEIGHT = (1.2D / 16D );
    private static final double LID_OPEN_LIP_Y_POS = (1D - LID_OPEN_LIP_HEIGHT);
    private static final double LID_OPEN_LIP_WIDTH = (2D / 16D);
    private static final double LID_OPEN_LIP_HORIZONTAL_OFFSET = (3.7D / 16D);

    private static final VoxelShape OPEN_BASE_X_SHAPE = Block.createCuboidShape(
            2F, 0F, 1F, 14F, 7F, 15F
    );

    private static final VoxelShape OPEN_BASE_Z_SHAPE = Block.createCuboidShape(
            1F, 0F, 2F, 15F, 7F, 14F
    );

    private static final VoxelShape CLOSED_BASE_X_SHAPE = Block.createCuboidShape(
            2F, 0F, 1F, 14F, 9F, 15F
    );

    private static final VoxelShape CLOSED_BASE_Z_SHAPE = Block.createCuboidShape(
            1F, 0F, 2F, 15F, 9F, 14F
    );

    public WickerBasketBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WickerBasketBE(pos, state);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getVoxelShape(state);
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return this.getVoxelShape(state);
    }

    /**
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        WickerBasketBE be = (WickerBasketBE) world.getBlockEntity(pos);

        if (be != null) {
            int slot = getSlot(hit, state.get(FACING));
            if (slot == -1 || !state.get(OPEN)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            ItemStack inv = be.getStack(slot);

            if (stack.isEmpty() && inv.isEmpty()) {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else if (stack.isEmpty()) {
                ItemStack taken = inv.copy();
                be.setStack(slot, ItemStack.EMPTY);
                be.playSoftSound(state);
                if (!player.getInventory().insertStack(taken)) {
                    be.dropStack(taken, 0.3f);
                }
            } else if (inv.isEmpty()) {
                be.setStack(slot, stack);
                player.setStackInHand(hand, ItemStack.EMPTY);
                be.playHarshSound(state);
            } else if (stack.isOf(inv.getItem()) && inv.getCount() < inv.getMaxCount()) {
                int maxCount = inv.getMaxCount();
                boolean overflow = stack.getCount() + inv.getCount() < inv.getMaxCount();
                int remainder = overflow ? (stack.getCount() + inv.getCount()) & maxCount : 0;

                inv.setCount(Math.min(stack.getCount() + inv.getCount(), maxCount));
                be.playHarshSound(state);
                stack.setCount(remainder);
            } else {
                ItemStack newStack = be.getStack(slot).copy();
                be.setStack(slot, stack);
                player.setStackInHand(hand, ItemStack.EMPTY);
                be.playHarshSound(state);
                if (!player.getInventory().insertStack(newStack)) {
                    be.dropStack(newStack, 0.3f);
                }
            }

            be.markDirty();
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.FAIL;
    }
     **/

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        WickerBasketBE be = (WickerBasketBE) world.getBlockEntity(pos);

        if (be != null) {
            int slot = getSlot(hit, state.get(FACING));
            if (slot == -1 || !state.get(OPEN)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

            ItemStack inv = be.getStack(slot);

            if (stack.isEmpty() && inv.isEmpty()) {
                return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            } else if (stack.isEmpty()) {
                ItemStack taken = inv.copy();
                be.setStack(slot, ItemStack.EMPTY);
                be.playSoftSound(state);
                if (!player.getInventory().insertStack(taken)) {
                    be.dropStack(taken, 0.3f);
                }
            } else if (inv.isEmpty()) {
                be.setStack(slot, stack);
                player.setStackInHand(hand, ItemStack.EMPTY);
                be.playHarshSound(state);
            } else if (stack.isOf(inv.getItem()) && inv.getCount() < inv.getMaxCount()) {
                int maxCount = inv.getMaxCount();
                boolean overflow = stack.getCount() + inv.getCount() < inv.getMaxCount();
                int remainder = overflow ? (stack.getCount() + inv.getCount()) & maxCount : 0;

                inv.setCount(Math.min(stack.getCount() + inv.getCount(), maxCount));
                be.playHarshSound(state);
                stack.setCount(remainder);
            } else {
                ItemStack taken = inv.copy();
                be.setStack(slot, ItemStack.EMPTY);
                be.playSoftSound(state);
                if (!player.getInventory().insertStack(taken)) {
                    be.dropStack(taken, 0.3f);
                }
            }

            be.markDirty();
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.FAIL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? TickableBlockEntity.getTicker() : null;
    }

    private VoxelShape getVoxelShape(BlockState state) {
        Direction.Axis axis = state.get(FACING).getAxis();
        VoxelShape baseOpen = axis == Direction.Axis.X ? OPEN_BASE_X_SHAPE : OPEN_BASE_Z_SHAPE;
        VoxelShape baseClosed = axis == Direction.Axis.X ? CLOSED_BASE_X_SHAPE : CLOSED_BASE_Z_SHAPE;
        VoxelShape openShape = VoxelShapes.union(baseOpen, this.getLidLipShape(state));

        return state.get(OPEN) ? openShape : baseClosed;
    }

    private VoxelShape getLidLipShape(BlockState state) {
        double minX = 0, maxX = 1;
        double minZ = 0, maxZ = 1;

        switch (state.get(FACING)) {
            case NORTH -> {
                minZ = LID_OPEN_LIP_HORIZONTAL_OFFSET;
                maxZ = minZ + LID_OPEN_LIP_WIDTH;
            }
            case SOUTH -> {
                maxZ = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET;
                minZ = maxZ - LID_OPEN_LIP_WIDTH;
            }
            case WEST -> {
                minX = LID_OPEN_LIP_HORIZONTAL_OFFSET;
                maxX = minX + LID_OPEN_LIP_WIDTH;
            }
            case EAST -> {
                maxX = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET;
                minX = maxX - LID_OPEN_LIP_WIDTH;
            }
        }

        return VoxelShapes.cuboid(minX, LID_OPEN_LIP_Y_POS, minZ, maxX, 1D, maxZ);
    }

    public static int getSlot(BlockHitResult hit, Direction dir) {
        Vec3d pos = rotateConstant(hit.getPos(), dir);
        Vec3d blockPos = rotateConstant(hit.getBlockPos().toCenterPos(), dir);

        double x = pos.x - blockPos.getX();
        double z = pos.z - blockPos.getZ();
        boolean onTop = hit.getSide().equals(Direction.UP);

        if (isClickingLid(hit)) return -1;

        int slot = -1;
        if (!onTop) return slot;
        if (Math.signum(x) == -1.0 && Math.signum(z) == -1.0) slot = 0;
        if (Math.signum(x) == 1.0 && Math.signum(z) == -1.0) slot = 1;
        if (Math.signum(x) == -1.0 && Math.signum(z) == 1.0) slot = 2;
        if (Math.signum(x) == 1.0 && Math.signum(z) == 1.0) slot = 3;

        return slot;
    }

    public static Vec3d rotateConstant(Vec3d vec, Direction dir) {
        return vec.rotateY((float) Math.toRadians(dir.asRotation()));
    }

    public static boolean isClickingLid(BlockHitResult hit) {
        double relativeY = hit.getPos().getY() - hit.getBlockPos().getY();
        return relativeY > BASKET_OPEN_HEIGHT;
    }

}