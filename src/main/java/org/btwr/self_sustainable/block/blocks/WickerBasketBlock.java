package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.entity.WickerBasketBE;
import org.btwr.self_sustainable.util.TickableBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class WickerBasketBlock extends AbstractPrimitiveStorageBlock {

    public static final MapCodec<WickerBasketBlock> CODEC = WickerBasketBlock.createCodec(WickerBasketBlock::new);

    private static final float BASKET_HEIGHT = 0.5F;
    private static final float BASKET_OPEN_HEIGHT = 0.75F;
    private static final float BASKET_RIM_WIDTH = 1F / 16F;
    private static final float BASKET_WIDTH_LIP = 0F / 16F;
    private static final float BASKET_DEPTH_LIP = 1F / 16F;
    private static final float BASKET_LID_HEIGHT = 2F / 16F;

    private static final double LID_OPEN_LIP_HEIGHT = 1D / 16D;
    private static final double LID_OPEN_LIP_Y_POS = 1D - LID_OPEN_LIP_HEIGHT;
    private static final double LID_OPEN_LIP_WIDTH = 2D / 16D;
    private static final double LID_OPEN_LIP_HORIZONTAL_OFFSET = 5D / 16D;

    private static final Map<Direction, VoxelShape> SHAPES_CLOSED = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> SHAPES_OPEN = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> SHAPES_OPEN_LID = new EnumMap<>(Direction.class);

    static {
        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            float minX, maxX, minZ, maxZ;

            if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                minX = BASKET_RIM_WIDTH + BASKET_WIDTH_LIP;
                maxX = 1F - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP;
                minZ = BASKET_RIM_WIDTH + BASKET_DEPTH_LIP;
                maxZ = 1F - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP;
            } else {
                minX = BASKET_RIM_WIDTH + BASKET_DEPTH_LIP;
                maxX = 1F - BASKET_RIM_WIDTH - BASKET_DEPTH_LIP;
                minZ = BASKET_RIM_WIDTH + BASKET_WIDTH_LIP;
                maxZ = 1F - BASKET_RIM_WIDTH - BASKET_WIDTH_LIP;
            }

            SHAPES_CLOSED.put(dir, VoxelShapes.cuboid(minX, 0F, minZ, maxX, BASKET_HEIGHT, maxZ));
            SHAPES_OPEN.put(dir, VoxelShapes.cuboid(minX, 0F, minZ, maxX, BASKET_OPEN_HEIGHT - BASKET_LID_HEIGHT, maxZ));

            double lipMinX, lipMaxX, lipMinZ, lipMaxZ;
            switch (dir) {
                case NORTH -> {
                    lipMinX = 0D;
                    lipMaxX = 1D;
                    lipMinZ = LID_OPEN_LIP_HORIZONTAL_OFFSET;
                    lipMaxZ = LID_OPEN_LIP_HORIZONTAL_OFFSET + LID_OPEN_LIP_WIDTH;
                }
                case SOUTH -> {
                    lipMinX = 0D;
                    lipMaxX = 1D;
                    lipMinZ = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET - LID_OPEN_LIP_WIDTH;
                    lipMaxZ = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET;
                }
                case WEST -> {
                    lipMinX = LID_OPEN_LIP_HORIZONTAL_OFFSET;
                    lipMaxX = LID_OPEN_LIP_HORIZONTAL_OFFSET + LID_OPEN_LIP_WIDTH;
                    lipMinZ = 0D;
                    lipMaxZ = 1D;
                }
                case EAST -> {
                    lipMinX = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET - LID_OPEN_LIP_WIDTH;
                    lipMaxX = 1D - LID_OPEN_LIP_HORIZONTAL_OFFSET;
                    lipMinZ = 0D;
                    lipMaxZ = 1D;
                }
                default -> {
                    lipMinX = 0D; lipMaxX = 1D;
                    lipMinZ = 0D; lipMaxZ = 1D;
                }
            }

            VoxelShape lipShape = VoxelShapes.cuboid(lipMinX, LID_OPEN_LIP_Y_POS, lipMinZ, lipMaxX, 1D, lipMaxZ);
            SHAPES_OPEN_LID.put(dir, VoxelShapes.union(SHAPES_OPEN.get(dir), lipShape));
        }
    }

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

    private VoxelShape getShape(BlockState state) {
        Direction facing = state.get(FACING);
        if (!state.get(OPEN)) {
            return SHAPES_CLOSED.getOrDefault(facing, SHAPES_CLOSED.get(Direction.NORTH));
        }
        if (state.get(OPEN)) {
            return SHAPES_OPEN_LID.getOrDefault(facing, SHAPES_OPEN_LID.get(Direction.NORTH));
        }
        return SHAPES_OPEN.getOrDefault(facing, SHAPES_OPEN.get(Direction.NORTH));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state);
    }

    @Override
    protected VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return getShape(state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? TickableBlockEntity.getTicker() : null;
    }
}