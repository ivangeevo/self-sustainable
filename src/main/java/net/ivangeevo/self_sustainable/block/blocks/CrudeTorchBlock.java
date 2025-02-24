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
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.world.World;

public class CrudeTorchBlock extends AbstractModTorchBlock {

    protected static final MapCodec<SimpleParticleType> PARTICLE_TYPE_CODEC = Registries.PARTICLE_TYPE
            .getCodec()
            .comapFlatMap(
                    particleType -> particleType instanceof SimpleParticleType simpleParticleType
                            ? DataResult.success(simpleParticleType)
                            : DataResult.error(() -> "Not a SimpleParticleType: " + particleType),
                    particleType -> particleType
            )
            .fieldOf("particle_options");

    public static final MapCodec<CrudeTorchBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    PARTICLE_TYPE_CODEC.forGetter(block -> block.particle),
                    createSettingsCodec()
            ).apply(instance, CrudeTorchBlock::new)
    );

    protected final SimpleParticleType particle;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public CrudeTorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings, TorchFireState fireState) {
        super(settings, particle, fireState);
        this.particle = particle;
    }

    public CrudeTorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings) {
        this(particle, settings, TorchFireState.UNLIT);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return AbstractModTorchBlock.validateTicker(type, ModBlockEntities.TORCH, TorchBE::tick);
    }


    @Override
    public boolean isWallTorch() { return false; }


}
