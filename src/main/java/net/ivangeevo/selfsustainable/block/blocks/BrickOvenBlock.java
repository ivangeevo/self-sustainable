package net.ivangeevo.selfsustainable.block.blocks;

import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.item.interfaces.ItemAdded;
import net.ivangeevo.selfsustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.selfsustainable.util.ItemUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BrickOvenBlock
        extends BlockWithEntity
        implements Waterloggable {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    /**
     * The shape used to test whether a given block is considered 'smokey'.
     */
    private static final VoxelShape SMOKEY_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final int field_31049 = 5;
    private final boolean emitsParticles;
    private final int fireDamage;


    protected final float clickYTopPortion = (6F / 16F );
    protected final float clickYBottomPortion = (6F / 16F );


    public BrickOvenBlock(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings) {
        super(settings);
        this.emitsParticles = emitsParticles;
        this.fireDamage = fireDamage;
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(SIGNAL_FIRE, false).with(WATERLOGGED, false).with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BrickOvenBlockEntity brickOvenBlockEntity;
        Optional<OvenCookingRecipe> optional;
        BlockEntity blockEntity = world.getBlockEntity(pos);

        double relativeClickY = hit.getPos().getY() - pos.getY();

        if (hit.getSide() != state.get(FACING)) {
            return ActionResult.FAIL;
        }

        if (relativeClickY > clickYTopPortion) {
            if (blockEntity instanceof BrickOvenBlockEntity) {
                brickOvenBlockEntity = (BrickOvenBlockEntity) blockEntity;
                ItemStack itemStack = player.getStackInHand(hand);

                // Check for cooking
                if ((optional = brickOvenBlockEntity.getRecipeFor(itemStack)).isPresent()) {
                    if (!world.isClient && brickOvenBlockEntity.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime()))
                    {
                        return ActionResult.SUCCESS;
                    }
                } else {
                    // Check for retrieving
                    brickOvenBlockEntity.retrieveItem(player, itemStack);
                    return ActionResult.CONSUME_PARTIAL;
                }
            }
        } else if (relativeClickY < clickYBottomPortion) {
            // Reuse the blockEntity from the first if block
            if (blockEntity instanceof BrickOvenBlockEntity) {
                brickOvenBlockEntity = (BrickOvenBlockEntity) blockEntity;
                ItemStack heldStack = player.getStackInHand(hand);

                int fuelTime = brickOvenBlockEntity.getFuelTimeForStack(heldStack);
                int itemsBurned = brickOvenBlockEntity.attemptToAddFuel(heldStack, fuelTime);

                // handle fuel here
                Item item = heldStack.getItem();

                if (((ItemAdded) item).getCanBeFedDirectlyIntoBrickOven(fuelTime)) {
                    if (!world.isClient) {

                        if (itemsBurned > 0) {
                            BlockPos soundPos = new BlockPos(
                                    (int) ((double) pos.getX() + 0.5D),
                                    (int) ((double) pos.getY() + 0.5D),
                                    (int) ((double) pos.getZ() + 0.5D));

                            if (state.get(LIT)) {
                                world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                                        0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);
                            } else {
                                world.playSound(null, soundPos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                                        0.25F, (world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F);
                            }

                            heldStack.decrement(1);
                        }
                    }

                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.FAIL;
    }







        @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.get(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(world.getDamageSources().inFire(), this.fireDamage);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrickOvenBlockEntity) {
            ItemScatterer.spawn(world, pos, ((BrickOvenBlockEntity)blockEntity).getItemsBeingCooked());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }



    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.DOWN) {
            return state.with(SIGNAL_FIRE, this.isSignalFireBaseBlock(neighborState));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean isSignalFireBaseBlock(BlockState state) {
        return state.isOf(Blocks.HAY_BLOCK);
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT).booleanValue()) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.emitsParticles && random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
            }
        }
    }

    public static void extinguish(@Nullable Entity entity, WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity;
        if (world.isClient()) {
            for (int i = 0; i < 20; ++i) {
                BrickOvenBlock.spawnSmokeParticle((World)world, pos, state.get(SIGNAL_FIRE), true);
            }
        }
        if ((blockEntity = world.getBlockEntity(pos)) instanceof BrickOvenBlockEntity) {
            ((BrickOvenBlockEntity)blockEntity).spawnItemsBeingCooked();
        }
        world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.get(Properties.WATERLOGGED).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            boolean bl = state.get(LIT);
            if (bl) {
                if (!world.isClient()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                BrickOvenBlock.extinguish(null, world, pos, state);
            }
            world.setBlockState(pos, (BlockState)((BlockState)state.with(WATERLOGGED, true)).with(LIT, false), Block.NOTIFY_ALL);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && !state.get(LIT).booleanValue() && !state.get(WATERLOGGED).booleanValue()) {
            world.setBlockState(blockPos, (BlockState)state.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
        Random random = world.getRandom();
        DefaultParticleType defaultParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        world.addImportantParticle(defaultParticleType, true, (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (lotsOfSmoke) {
            world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }


    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return (BlockState)state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            if (state.get(LIT)) {
                return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::clientTick);
            }
        } else {
            if (state.get(LIT)) {
                return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::litServerTick);
            }
            return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::unlitServerTick);
        }
        return null;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public static boolean canBeLit(BlockState state) {
        return state.isIn(BlockTags.CAMPFIRES, statex -> statex.contains(WATERLOGGED) && statex.contains(LIT)) && !state.get(WATERLOGGED) && !state.get(LIT);
    }
}
