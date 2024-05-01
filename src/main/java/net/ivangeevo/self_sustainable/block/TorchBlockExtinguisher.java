package net.ivangeevo.self_sustainable.block;

import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

/** A custom class to act as a fully fledged alternative for mixing into the TorchBlock.class
 * while providing additional functionality.
 */
public class TorchBlockExtinguisher extends TorchBlock implements BlockEntityProvider
{

    public static BooleanProperty LIT = Properties.LIT;


    public TorchBlockExtinguisher(Settings settings, ParticleEffect particle)
    {
        super(settings, particle);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient) {
            return state.get(LIT) ? checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::clientTick) : null;
        } else {
            return checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::serverTick);
        }
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Unique
    private void playLitFX(World world, BlockPos pos)
    {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);

    }

    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return false;
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return false;
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TorchBlockEntity(pos, state);
    }


    @Override
    public void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
}
