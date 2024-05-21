package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.ivangeevo.self_sustainable.block.interfaces.TorchBlockAdded;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block implements TorchBlockAdded, BlockEntityProvider
{
    public TorchBlockMixin(Settings settings)
    {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setDefaultState(Settings settings, ParticleEffect particle, CallbackInfo ci)
    {
        this.setDefaultState(this.stateManager.getDefaultState().with(TorchBlockAdded.LIT, false));
    }

    @Inject(method = "randomDisplayTick", at = @At("HEAD"), cancellable = true)
    private void cancelParticles(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (!state.get(TorchBlockAdded.LIT))
        {
            ci.cancel();
        }
    }

    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(TorchBlockAdded.LIT);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {

        if (!world.isClient() && !state.get(TorchBlockAdded.LIT))
        {
            world.setBlockState(pos, state.with(TorchBlockAdded.LIT, true));
            this.playLitFX(world, pos);
            return ActionResult.SUCCESS;
        }

        return super.onUse(state, world, pos, player, hand, hit);

    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TorchBlockEntity(pos, state);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return state.get(TorchBlockAdded.LIT) ? checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::clientTick) : null;
        } else {
            return checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::serverTick);
        }
    }

    private void playLitFX(World world, BlockPos pos)
    {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);

    }

    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

}
