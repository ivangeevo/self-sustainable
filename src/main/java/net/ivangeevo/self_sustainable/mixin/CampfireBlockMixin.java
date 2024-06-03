package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.CampfireBlockManager;
import net.ivangeevo.self_sustainable.block.VariableCampfireBE;
import net.ivangeevo.self_sustainable.block.interfaces.*;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.ivangeevo.self_sustainable.block.entity.CampfireBEManager.isRainingOnCampfire;
import static net.minecraft.block.CampfireBlock.SIGNAL_FIRE;


@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements Ignitable, CampfireBlockAdded, IVariableCampfireBlock
{
    @Shadow @Final private boolean emitsParticles;
    @Shadow @Final private int fireDamage;
    @Shadow @Final public static BooleanProperty WATERLOGGED;

    @Shadow protected abstract boolean isSignalFireBaseBlock(BlockState state);

    @Shadow @Final public static DirectionProperty FACING;

    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedConstructor(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci)
    {
        //this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(FIRE_LEVEL, 0).with(FUEL_STATE, CampfireState.NORMAL).with(HAS_SPIT, false));
        this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(FIRE_LEVEL, 0).with(FUEL_STATE, CampfireState.NORMAL).with(HAS_SPIT, false));

    }

    @Inject(method = "createBlockEntity", at = @At("HEAD"), cancellable = true)
    private void injectedBE(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir)
    {
        cir.setReturnValue(new VariableCampfireBE(pos, state));
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir)
    {
        BlockPos blockPos;
        World worldAccess = context.getWorld();
        boolean bl = worldAccess.getFluidState(blockPos = context.getBlockPos()).getFluid() == Fluids.WATER;
        cir.setReturnValue(
                this.getDefaultState()
                        .with(WATERLOGGED, bl)
                        .with(SIGNAL_FIRE, this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down())))
                        .with(FIRE_LEVEL, 0).with(FACING, context.getHorizontalPlayerFacing()) );
    }

    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void addedCustomProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
    {
        CampfireBlockManager.appendCustomProperties(builder);
        ci.cancel();
    }

    // Change LIT for FIRE_LEVEL here too.
    @Inject(method = "tryFillWithFluid", at = @At("HEAD"), cancellable = true)
    private void injectedTryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir)
    {
        if (!state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER)
        {
            int fl = state.get(FIRE_LEVEL);
            if (fl > 0)
            {
                if (!world.isClient())
                {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                CampfireBlock.extinguish(null, world, pos, state);
            }
            world.setBlockState(pos, state.with(WATERLOGGED, true).with(FIRE_LEVEL, 0), Block.NOTIFY_ALL);
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
        if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && state.get(FIRE_LEVEL) < 1 && !state.get(WATERLOGGED)) {
            world.setBlockState(blockPos, state.with(FIRE_LEVEL, 1), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    @Inject(method = "isLitCampfire", at = @At("HEAD"), cancellable = true)
    private static void injectedIsLitCampfire(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue( state.contains(FIRE_LEVEL) && state.isIn(BlockTags.CAMPFIRES) && state.get(FIRE_LEVEL) > 0 );
    }


    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCustomOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(CampfireBlockManager.setCustomShapes(state));
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (state.getBlock() == Blocks.CAMPFIRE)
        {
            cir.setReturnValue(CampfireBlockManager.onUse(state, world, pos, player, hand, hit));
        }


    }

    // Change LIT for FIRE LEVEL greater than 1 when checking to damage entities.
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void injectedOnEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        if (state.get(FIRE_LEVEL) > 1 && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(world.getDamageSources().inFire(), this.fireDamage);
        }
        super.onEntityCollision(state, world, pos, entity);
        ci.cancel();

    }

    // Making it randomly display only if the FIRE Level is more than 0, instead of the LIT property.
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (state.get(FIRE_LEVEL) <= 0)
        {
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
        ci.cancel();
    }

    /**
     * @author
     * @reason couldn't get it to work otherwise than overwrite. maybe its possible though.
     */
    @Overwrite
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient)
        {
            if (state.get(FIRE_LEVEL) > 0)
            {
                return checkType(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::clientTick);
            }
        }
        else
        {
            if (state.get(FIRE_LEVEL) > 0)
            {
                return checkType(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::litServerTick);
            }
            return checkType(type, ModBlockEntities.CAMPFIRE, VariableCampfireBE::unlitServerTick);
        }
        return null;
    }


    @Inject(method = "canBeLit", at = @At("HEAD"), cancellable = true)
    private static void injectedCanBeLit(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue( state.isIn(BlockTags.CAMPFIRES, statex -> statex.contains(WATERLOGGED) && statex.contains(FIRE_LEVEL)) && !state.get(WATERLOGGED) && state.get(FIRE_LEVEL) <= 0 );

    }
    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return true;
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return blockAccess.getBlockState(pos).get(FIRE_LEVEL) == 0 && getFuelState(blockAccess.getBlockState(pos)) == CampfireState.NORMAL;
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos)
    {
        if (this.getCanBeSetOnFireDirectly(world, pos))
        {

            if (!isRainingOnCampfire(world, pos))
            {
                changeFireLevel(world, pos, 1);

                VariableCampfireBE campfireBE = (VariableCampfireBE) world.getBlockEntity(pos);

                assert campfireBE != null;
                ((CampfireBlockEntityAdded)campfireBE).onFirstLit();

                BlockPos soundPos =
                        new BlockPos(
                                (int) (pos.getX() + 0.5D),
                                (int) (pos.getY() + 0.5D),
                                (int) (pos.getZ() + 0.5D));

                world.playSound(null, soundPos,
                        SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 1F,
                        world.random.nextFloat() * 0.4F + 0.8F);

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
            }
            else
            {
                Ignitable.playExtinguishSound(world, pos, false);
            }

            return true;
        }

        return false;
    }

    @Override
    public void changeFireLevel(World world, BlockPos pos, int fireLevel)
    {
        //CampfireBlock.campfireChangingState = true;

        BlockState tempState = world.getBlockState(pos);
        world.setBlockState( pos, tempState.with(FIRE_LEVEL, fireLevel), Block.NOTIFY_ALL);

        //CampfireBlock.campfireChangingState = false;
    }
    @Override
    public CampfireState getFuelState(BlockState state) {
        return state.get(FUEL_STATE);
    }

    @Override
    public void relightFire(World world, BlockPos pos) {
        changeFireLevel(world, pos, setFuelState(world, pos, CampfireState.NORMAL));

    }

    @Override
    public int getFireLevel(BlockState state) {
        return state.get(FIRE_LEVEL);
    }

    @Override
    public BlockState setFireLevel (BlockState state, int newLevel)
    {
        return state.with(FIRE_LEVEL, newLevel);
    }

    @Override
    public void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            setFuelState(world, pos, CampfireState.SMOULDERING);
        }
        else
        {
            setFuelState(world, pos, CampfireState.BURNED_OUT);
        }

        changeFireLevel(world, pos, 0);

        if ( !world.isClient() )
        {
            Ignitable.playExtinguishSound(world, pos, true);
        }
    }

    @Override
    public void stopSmouldering(World world, BlockPos pos)
    {
        setFuelState(world,pos, CampfireState.BURNED_OUT);
    }

    // Method to set the fuel state

    public int setFuelState(World world, BlockPos pos, CampfireState fuelState)
    {
        BlockState state = world.getBlockState(pos);
        return setCampfireState(state, setCampfireState(state, fuelState)).ordinal();
    }

    public CampfireState setCampfireState(BlockState state, CampfireState fuelState)
    {
      return state.get(FUEL_STATE);
    }

    public void relightFire(World world, BlockPos pos, BlockState state)
    {
        changeFireLevel(world, pos, 1);
    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        Optional<NetherPortal> optional;
        if (oldState.isOf(state.getBlock()))
        {
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
        if (!state.canPlaceAt(world, pos))
        {
            world.removeBlock(pos, false);
        }

    }

    @Unique
    private static boolean isOverworldOrNether(World world)
    {
        return world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.NETHER;
    }




    @Override
    public BlockState getAppearance(BlockState state, BlockRenderView renderView, BlockPos pos, Direction side, @Nullable BlockState sourceState, @Nullable BlockPos sourcePos) {
        return super.getAppearance(state, renderView, pos, side, sourceState, sourcePos);
    }
}
