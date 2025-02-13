package net.ivangeevo.self_sustainable.block.blocks;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ivangeevo.self_sustainable.block.entity.TorchBE;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.function.IntSupplier;

public class ModTorchBlock extends AbstractModTorchBlock {

    protected static final MapCodec<SimpleParticleType> PARTICLE_TYPE_CODEC = Registries.PARTICLE_TYPE
            .getCodec()
            .comapFlatMap(
                    particleType -> particleType instanceof SimpleParticleType simpleParticleType
                            ? DataResult.success(simpleParticleType)
                            : DataResult.error(() -> "Not a SimpleParticleType: " + particleType),
                    particleType -> particleType
            )
            .fieldOf("particle_options");

    public static final MapCodec<ModTorchBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    PARTICLE_TYPE_CODEC.forGetter(block -> block.particle),
                    createSettingsCodec()
            ).apply(instance, ModTorchBlock::new)
    );

    protected final SimpleParticleType particle;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return AbstractModTorchBlock.validateTicker(type, ModBlockEntities.TORCH, TorchBE::tick);
    }

    public ModTorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings, TorchFireState fireState) {
        super(settings, particle, fireState);
        this.particle = particle;
    }

    public ModTorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings) {
        this(particle, settings, TorchFireState.UNLIT);
    }

    @Override
    public boolean isWallTorch() { return false; }


}
