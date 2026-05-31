package org.btwr.self_sustainable.mixin.block;

import org.btwr.self_sustainable.block.CampfireBlockMixinManager;
import org.btwr.self_sustainable.block.entity.VariableCampfireBE;
import org.btwr.self_sustainable.block.interfaces.added.CampfireBlockAdded;
import org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.block.utils.CampfireState;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.dimension.NetherPortal;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;

// TODO: Make campfire spread fire to neighbouring campfires and also to set fire around like it does in BTW
@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements IgnitableBlock, CampfireBlockAdded, IVariableCampfireBlock
{
    @Shadow @Final private boolean emitsParticles;
    @Shadow @Final private int fireDamage;
    @Shadow @Final public static BooleanProperty WATERLOGGED;
    @Shadow @Final public static BooleanProperty SIGNAL_FIRE;
    @Shadow protected abstract boolean isSignalFireBaseBlock(BlockState state);
    @Shadow @Final public static DirectionProperty FACING;

    @Unique private static final CampfireBlockMixinManager managerInstance = CampfireBlockMixinManager.getInstance();

    @Unique Ingredient fuels;

    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    // Makes the campfire not solid (able to walk through)
    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedConstructorSettings(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci) {
        settings.notSolid();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedDefaultState(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci) {
        this.setDefaultState(
                this.getStateManager().getDefaultState()
                        .with(LIT, false)
                        .with(FIRE_LEVEL, 0)
                        .with(FUEL_STATE, CampfireState.NORMAL)
                        .with(HAS_SPIT, false)
        );
    }

    // Needed inject to cancel vanilla interactions for the normal campfire while still allowing other item's to interact with the block
    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void btwr$overrideOnUse(
            ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> cir
    ) {
        // If this campfire is managed by your system
        if (state.isOf(Blocks.CAMPFIRE)) {
            // Let your UseBlockCallback handle it instead
            cir.setReturnValue(ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify)
    {
        boolean isSolidBlockBelow = world.getBlockState(pos.down()).isSolidBlock(world, pos.down());

        // break block & drop loot when block below is removed
        if (!isSolidBlockBelow) {
            world.breakBlock(pos, true);
        }

        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSolidBlock(world, pos.down());
    }

    @Inject(method = "createBlockEntity", at = @At("HEAD"), cancellable = true)
    private void injectedBE(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        if (state.isOf(Blocks.CAMPFIRE)) {
            cir.setReturnValue(new VariableCampfireBE(pos, state));
        }
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        BlockPos blockPos;
        World worldAccess = context.getWorld();
        boolean bl = worldAccess.getFluidState(blockPos = context.getBlockPos()).getFluid() == Fluids.WATER;

        cir.setReturnValue(this.getDefaultState()
                        .with(WATERLOGGED, bl)
                        .with(SIGNAL_FIRE, this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down())))
                        .with(FIRE_LEVEL, 0)
                        .with(FACING, context.getHorizontalPlayerFacing())
        );
    }

    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void addedCustomProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        managerInstance.appendCustomProperties(builder);
        ci.cancel();
    }

    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void onOnStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof VariableCampfireBE) {
            ItemScatterer.spawn(world, pos, ((VariableCampfireBE)blockEntity).getItemsBeingCooked());
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    // Change LIT for FIRE_LEVEL here too.
    @Inject(method = "tryFillWithFluid", at = @At("HEAD"), cancellable = true)
    private void injectedTryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir)
    {
        if (!state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
            int fl = state.get(FIRE_LEVEL);
            if (fl > 0) {
                btwr$extinguishFire((World) world, state, pos, false);
            }

            world.setBlockState(pos,
                    world.getBlockState(pos).with(WATERLOGGED, true),
                    Block.NOTIFY_ALL
            );
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));

            cir.setReturnValue(true);
        }

        cir.setReturnValue(false);
    }

    // Change LIT to FIRE LEVEL again.
    @Inject(method = "onProjectileHit", at = @At("HEAD"))
    private void injectedOnProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci)
    {
        BlockPos blockPos = hit.getBlockPos();
        if (!world.isClient && projectile.isOnFire() &&
                projectile.canModifyAt(world, blockPos) && state.get(FIRE_LEVEL) < 1 && !state.get(WATERLOGGED))
        {
            //world.setBlockState(blockPos, state.with(FIRE_LEVEL, 1).with(LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            btwr$changeFireLevel(world, blockPos, 1);
        }
    }

    @Inject(method = "isLitCampfire", at = @At("HEAD"), cancellable = true)
    private static void injectedIsLitCampfire(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.contains(FIRE_LEVEL) && state.isIn(BlockTags.CAMPFIRES) && state.get(FIRE_LEVEL) > 1);
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCustomOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(managerInstance.setCustomShapes(state));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void injectedOnEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        boolean canBurn = state.get(FIRE_LEVEL) > 1;

        if (canBurn && entity instanceof LivingEntity) {
            entity.damage(world.getDamageSources().inFire(), this.fireDamage);
        }

        // make items burn on touch & fuels add to fuel time of campfire
        if (entity instanceof ItemEntity itemEntity) {

            ItemStack stack = itemEntity.getStack();
            if (world.getBlockEntity(pos) instanceof VariableCampfireBE campfireBE) {
                int itemBurnTime = managerInstance.getItemFuelTime(stack);

                if (!world.isClient) {
                    // burned out doesn't allow items to burn in it, duh
                    if (state.get(FUEL_STATE) == CampfireState.BURNED_OUT) {
                        return;
                    }
                    this.fuels = Ingredient.ofStacks(this.getAllowedFuels().stream().filter(item -> item.isEnabled(world.getEnabledFeatures())).map(ItemStack::new));


                    // fuel items can burn at fuel level 1(or higher) or smouldering
                    if (this.fuels.test(stack)) {
                        if (state.get(FIRE_LEVEL) > 0 || state.get(FUEL_STATE) == CampfireState.SMOULDERING) {
                            campfireBE.addBurnTime(state, stack, itemBurnTime);
                            stack.decrement(stack.getCount());
                            world.playSound(
                                    null,
                                    BlockPos.ofFloored(pos.toCenterPos()),
                                    ModSoundEvents.CAMPFIRE_IGNITE, SoundCategory.BLOCKS,
                                    0.2F + world.random.nextFloat() * 0.1F,
                                    world.random.nextFloat() * 0.25F + 1.25F
                            );
                        }
                        // all other items burn at fuel level higher than 1
                    }
                    else if (state.get(FIRE_LEVEL) > 1) {
                        stack.decrement(stack.getCount());
                        world.playSound(
                                null,
                                BlockPos.ofFloored(pos.toCenterPos()),
                                ModSoundEvents.CAMPFIRE_IGNITE, SoundCategory.BLOCKS,
                                0.2F + world.random.nextFloat() * 0.1F,
                                world.random.nextFloat() * 0.25F + 1.25F
                        );
                    }
                }
            }

        }

        super.onEntityCollision(state, world, pos, entity);
        ci.cancel();
    }

    private boolean canItemAddToFuelTime(World world, BlockState state, ItemStack stack) {
        // if thrown item is in the fuel time map and also if the campfire is in an appropriate state to accept fuel items
        return this.fuels.test(stack) && (state.get(FIRE_LEVEL) > 0 || state.get(FUEL_STATE) == CampfireState.SMOULDERING);
    }

    protected Set<Item> getAllowedFuels() {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().keySet();
    }

    // Making it randomly display only if the FIRE Level is more than 0, instead of the LIT property.
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.get(FIRE_LEVEL) <= 0) {
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

        if (state.get(FIRE_LEVEL) > 0 ) {
            if (random.nextInt(24) == 0) {
                float fVolume = (state.get(FIRE_LEVEL) * 0.25F ) + random.nextFloat();

                double d = (double) pos.getX() + 0.5;
                double e = pos.getY();
                double f = (double) pos.getZ() + 0.5;

                world.playSound(d, e, f, ModSoundEvents.CAMPFIRE_BURNING,
                        SoundCategory.BLOCKS, fVolume,
                        random.nextFloat() * 0.7F + 0.3F, false);
            }
        }

        ci.cancel();
    }

    @Unique
    private static boolean isRainingOnCampfire(World world, BlockPos pos) {
        return world.isRaining() && world.hasRain(pos);
    }

    /**
     * @author ivangeevo
     * @reason Couldn't get it to work otherwise than overwrite. maybe its possible though.
     */
    @Overwrite
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            if (state.get(FIRE_LEVEL) > 0) {
                return validateTicker(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::clientTick);
            }
        }
        else {
            if (state.get(FIRE_LEVEL) > 0) {
                return validateTicker(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::litServerTick);
            }
            return validateTicker(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::unlitServerTick);
        }
        return null;
    }

    @Inject(method = "canBeLit", at = @At("HEAD"), cancellable = true)
    private static void injectedCanBeLit(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.isIn(BlockTags.CAMPFIRES,
                statex -> statex.contains(WATERLOGGED) && statex.contains(FIRE_LEVEL) && statex.contains(FUEL_STATE))
                && !state.get(WATERLOGGED) && state.get(FIRE_LEVEL) <= 0 && !(state.get(FUEL_STATE) == CampfireState.BURNED_OUT));
    }

    @Override
    public int btwr$getChanceOfFireSpreadingDirectlyTo(WorldAccess blockAccess, BlockPos pos) {
        if (blockAccess.getBlockState(pos).get(FIRE_LEVEL) == 0 && btwr$getCampfireState(blockAccess.getBlockState(pos)) == CampfireState.NORMAL)
        {
            return 60; // same chance as leaves and other highly flammable objects
        }

        return 0;
    }

    @Override
    public boolean btwr$getCanBeSetOnFireDirectly(@NotNull WorldAccess blockAccess, BlockPos pos) {
        return blockAccess.getBlockState(pos).get(FIRE_LEVEL) == 0 && btwr$getCampfireState(blockAccess.getBlockState(pos)) == CampfireState.NORMAL;
    }

    @Override
    public boolean btwr$getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {

        BlockState state = blockAccess.getBlockState(pos);
        /**
        return !state.get(LIT) && state.get(FIRE_LEVEL) == 0;
         **/
        return state.get(FIRE_LEVEL) == 0;
    }

    @Override
    public boolean btwr$setOnFireDirectly(World world, BlockPos pos) {
        if (this.btwr$getCanBeSetOnFireDirectly(world, pos)) {
            if (!isRainingOnCampfire(world, pos)) {
                btwr$changeFireLevel(world, pos, 1);

                VariableCampfireBE campfireBE = (VariableCampfireBE) world.getBlockEntity(pos);

                assert campfireBE != null;
                campfireBE.onFirstLit();

                BlockPos soundPos = new BlockPos((int) (pos.getX() + 0.5D), (int) (pos.getY() + 0.5D), (int) (pos.getZ() + 0.5D));

                world.playSound(null, soundPos, ModSoundEvents.CAMPFIRE_IGNITE, SoundCategory.BLOCKS, 1F,
                        world.random.nextFloat() * 0.4F + 0.8F
                );

                //TODO?: Add portal creation logic with campfire.
                /**
                if (!Block.portal.tryToCreatePortal(world, i, j, k)) {
                    // FCTODO: A bit hacky here.  Should probably be a general way to start a
                    // bigger fire atop flammable blocks

                    int iBlockBelowID = world.getBlockId(i, j - 1, k);

                    if (iBlockBelowID == Block.netherrack.blockID || iBlockBelowID == BTWBlocks.fallingNetherrack.blockID) {
                        world.setBlockWithNotify(i, j, k, Block.fire.blockID);
                    }
                }
                 **/
            } else {
                float fizzPitch = 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F;
                world.playSound(
                        null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                        SoundCategory.BLOCKS, 0.5F, fizzPitch
                );
            }

            return true;
        }

        return false;
    }

    @Override
    public void btwr$changeFireLevel(World world, BlockPos pos, int fireLevel) {
        BlockState tempState = world.getBlockState(pos);
        if (!world.isClient) {
            //world.setBlockState(pos, tempState.with(FIRE_LEVEL, fireLevel), Block.NOTIFY_ALL);
            BlockState state = world.getBlockState(pos);

            world.setBlockState(
                    pos,
                    state.with(FIRE_LEVEL, fireLevel).with(LIT, fireLevel > 0),
                    Block.NOTIFY_ALL
            );
        }
    }

    @Override
    public CampfireState btwr$getCampfireState(BlockState state) {
        return state.get(FUEL_STATE);
    }

    @Override
    public void btwr$relightFire(World world, BlockPos pos) {
        btwr$changeFireLevel(world, pos, 1);
    }

    @Override
    public int btwr$getFireLevel(BlockState state) {
        return state.get(FIRE_LEVEL);
    }

    @Override
    public BlockState btwr$setFireLevel (BlockState state, int newLevel)
    {
        return state.with(FIRE_LEVEL, newLevel);
    }

    @Override
    public void btwr$extinguishFire(World world, BlockState state, BlockPos pos, boolean smoulder) {
        if (smoulder) {
            setFuelState(world, pos, CampfireState.SMOULDERING);
        }
        else {
            setFuelState(world, pos, CampfireState.BURNED_OUT);
        }

        btwr$changeFireLevel(world, pos, 0);

        if (!world.isClient()) {
            float fizzPitch = 1F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F;
            world.playSound(
                    null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.BLOCKS, 0.1F, fizzPitch
            );
        }
    }

    @Override
    public void btwr$stopSmouldering(World world, BlockPos pos) {
        setFuelState(world, pos, CampfireState.BURNED_OUT);
    }

    // Method to set the fuel state
    public void setFuelState(World world, BlockPos pos, CampfireState fuelState) {
        BlockState state = world.getBlockState(pos);
        world.setBlockState(pos, state.with(FUEL_STATE, fuelState));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        Optional<NetherPortal> optional;
        if (oldState.isOf(state.getBlock())) {
            return;
        }

        /**
        if (isOverworldOrNether(world)
                && (optional = NetherPortal.getNewPortal(world, pos, Direction.Axis.X)).isPresent() && state.get(LIT))
        {
            optional.get().createPortal();
            return;
        }
         **/

        if (!state.canPlaceAt(world, pos)) {
            world.removeBlock(pos, false);
        }
    }

}