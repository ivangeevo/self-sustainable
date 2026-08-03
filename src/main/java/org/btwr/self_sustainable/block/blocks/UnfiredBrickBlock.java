package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.util.math.Box;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.entity.UnfiredBrickBE;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.jetbrains.annotations.Nullable;

public class UnfiredBrickBlock extends BlockWithEntity {

    public static final float BRICK_HEIGHT = (4F / 16F );
    public static final float BRICK_WIDTH = (6F / 16F );
    public static final float BRICK_HALF_WIDTH = (BRICK_WIDTH / 2F );
    public static final float BRICK_LENGTH = (12F / 16F );
    public static final float BRICK_HALF_LENGTH = (BRICK_LENGTH / 2F );
    protected static final VoxelShape SHAPE_Z_AXIS = VoxelShapes.cuboid((0.5F - BRICK_HALF_LENGTH), 0D, (0.5F - BRICK_HALF_WIDTH), (0.5F + BRICK_HALF_LENGTH), BRICK_HEIGHT, (0.5F + BRICK_HALF_WIDTH));
    protected static final VoxelShape SHAPE_X_AXIS = VoxelShapes.cuboid((0.5F - BRICK_HALF_WIDTH), 0D, (0.5F - BRICK_HALF_LENGTH), (0.5F + BRICK_HALF_WIDTH), BRICK_HEIGHT, (0.5F + BRICK_HALF_LENGTH));

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty DRYING_LEVEL = IntProperty.of("drying_level", 0, 7);

    public static final MapCodec<UnfiredBrickBlock> CODEC = Block.createCodec(UnfiredBrickBlock::new);

    @Override
    protected MapCodec<? extends UnfiredBrickBlock> getCodec() {
        return CODEC;
    }

    public UnfiredBrickBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DRYING_LEVEL);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return state.get(FACING).getAxis() == Direction.Axis.Z ? SHAPE_Z_AXIS : SHAPE_X_AXIS;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return super.getCollisionShape(state, world, pos, context);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(DRYING_LEVEL, 0).with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity instanceof LivingEntity) {
            VoxelShape shape = getOutlineShape(state, world, pos, ShapeContext.of(entity));
            Box shapeBox = shape.getBoundingBox().offset(pos);
            Box entityBox = entity.getBoundingBox();

            boolean horizontalOverlap = entityBox.maxX > shapeBox.minX && entityBox.minX < shapeBox.maxX
                    && entityBox.maxZ > shapeBox.minZ && entityBox.minZ < shapeBox.maxZ;
            boolean onTop = entityBox.minY >= shapeBox.minY && entityBox.minY <= shapeBox.maxY;

            if (horizontalOverlap && onTop) {
                // break block
                world.playSound(null, pos, ModSoundEvents.STEP_ON_WET_BRICK, SoundCategory.BLOCKS, (0.5F + 1.0F) / 2.0F, 0.1F * 0.8F);
                world.addBlockBreakParticles(pos, state);
                dropBlockAsItem(world, pos);
                world.removeBlock(pos, false);
            }
        }

        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }

        if (newState.getBlock() != ModBlocks.BRICK) {
            world.playSound(null, pos, SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.BLOCKS, (0.5F + 1.0F) / 2.0F, 0.1F * 0.8F );
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        if (!world.getBlockState(pos.down()).isSolidBlock(world, pos.down())) {
            //dropBlockAsItem(world, pos);
            Block.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    public void onFinishedCooking(World world, BlockPos pos, BlockState state) {
        BlockState dryState = ModBlocks.BRICK.getDefaultState().with(FACING, state.get(FACING));
        world.setBlockState(pos, dryState);
    }

    @Override public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return false;
    }


    private void dropBlockAsItem(World world, BlockPos pos) {
        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), Items.CLAY_BALL.getDefaultStack());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return UnfiredBrickBlock.validateTicker(type, ModBlockEntities.BRICK_UNFIRED, UnfiredBrickBE::tick);
    }


    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Drying particles display when its drying(only during daytime)
        long timeOfDay = world.getTimeOfDay() % 24000; // Get the time of day (0 - 23999)

        if (timeOfDay >= 0 && timeOfDay < 12000 && !world.hasRain(pos)) {

            double d = pos.getX() + 0.25F + world.random.nextFloat() * 0.5F;
            double e = pos.getY() + 0.5F + world.random.nextFloat() * 0.25F;
            double f = pos.getZ() + 0.25F + world.random.nextFloat() * 0.5F;

            if (world.random.nextInt( 20 ) == 0) {
                world.addParticle(ParticleTypes.CLOUD, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    public int getDryLevel(WorldAccess blockAccess, BlockPos pos) {
        return getDryLevel(blockAccess.getBlockState(pos));
    }

    public int getDryLevel(BlockState state)
    {
        return state.get(DRYING_LEVEL) >> 1;
    }

    public static void setDryingLevel(World world, BlockPos pos, int cookLevel) {
        BlockState currentState = world.getBlockState(pos);
        BlockState newState = setDryingLevel(currentState, cookLevel);
        world.setBlockState(pos, newState, 3);
    }

    public static BlockState setDryingLevel(BlockState state, int iCookLevel) {
        return state.with(DRYING_LEVEL, iCookLevel);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UnfiredBrickBE(pos, state);
    }

}