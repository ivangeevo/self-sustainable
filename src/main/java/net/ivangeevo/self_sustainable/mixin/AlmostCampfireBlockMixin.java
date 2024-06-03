package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.CampfireBlockManager;
import net.ivangeevo.self_sustainable.block.VariableCampfireBlock;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// this variation of the mixin is
// an almost fully working implementation where it gets the methods from the new VariableCampfireBlock fully
// (no static methods like before in CampfireBlockManager, even though some of them are still from there)
@Mixin(CampfireBlock.class)
public abstract class AlmostCampfireBlockMixin extends BlockWithEntity implements Ignitable, CampfireBlockAdded, IVariableCampfireBlock
{

    protected AlmostCampfireBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedConstructor(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci)
    {
        this.setDefaultState(this.getStateManager().getDefaultState().with(FIRE_LEVEL, 0).with(FUEL_STATE, CampfireState.NORMAL).with(HAS_SPIT, false));
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir)
    {
      cir.setReturnValue(  VariableCampfireBlock.getInstance().getPlacementState(context) );

    }

    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void addedCustomProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
    {
        CampfireBlockManager.appendCustomProperties(builder);
        ci.cancel();
    }

    // Change LIT for FIRE_LEVEL here too.
    @Inject(method = "tryFillWithFluid", at = @At("HEAD"))
    private void injectedTryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir)
    {
        VariableCampfireBlock.getInstance().tryFillWithFluid(world, pos, state, fluidState);
        cir.cancel();
    }

    // Change LIT to FIRE LEVEL again.
    @Inject(method = "onProjectileHit", at = @At("HEAD"), cancellable = true)
    private void injectedOnProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile, CallbackInfo ci)
    {
        VariableCampfireBlock.getInstance().onProjectileHit(world, state, hit, projectile);
        ci.cancel();
    }

    @Inject(method = "isLitCampfire", at = @At("HEAD"), cancellable = true)
    private static void injectedIsLitCampfire(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue( VariableCampfireBlock.isLitCampfire(state) );
    }


    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCustomOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue( CampfireBlockManager.setCustomShapes(state) );
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (state.getBlock() == Blocks.CAMPFIRE)
        {
            cir.setReturnValue(VariableCampfireBlock.getInstance().onUse(state, world, pos, player, hand, hit));
        }


    }

    // Change LIT for FIRE LEVEL greater than 1 when checking to damage entities.
    @Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
    private void injectedOnEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci)
    {
        VariableCampfireBlock.getInstance().onEntityCollision(state, world, pos, entity);
        ci.cancel();

    }

    // Making it randomly display only if the FIRE Level is more than 0, instead of the LIT property.
    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void injectedRandomDisplayTick(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
    {
        VariableCampfireBlock.getInstance().randomDisplayTick(state, world, pos, random);
        ci.cancel();
    }

    @Inject(method = "getTicker", at = @At("HEAD"), cancellable = true)
    private void injectGetTicker(World world, BlockState state, BlockEntityType<?> type, CallbackInfoReturnable<BlockEntityTicker<?>> cir)
    {
        cir.setReturnValue( VariableCampfireBlock.getInstance().getTicker(world, state, type) );
    }


    @Inject(method = "canBeLit", at = @At("HEAD"), cancellable = true)
    private static void injectedCanBeLit(BlockState state, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue( VariableCampfireBlock.canBeLit(state) );
    }

}
