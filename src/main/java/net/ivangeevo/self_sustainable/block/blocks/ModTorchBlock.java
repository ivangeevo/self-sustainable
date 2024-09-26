package net.ivangeevo.self_sustainable.block.blocks;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;

import java.util.function.IntSupplier;

/**
public class ModTorchBlock extends AbstractModTorchBlock
{

    protected static final MapCodec<SimpleParticleType> PARTICLE_TYPE_CODEC = Registries.PARTICLE_TYPE
            .getCodec()
            .<SimpleParticleType>comapFlatMap(
                    particleType -> particleType instanceof SimpleParticleType simpleParticleType
                            ? DataResult.success(simpleParticleType)
                            : DataResult.error(() -> "Not a SimpleParticleType: " + particleType),
                    particleType -> particleType
            )
            .fieldOf("particle_options");
    public static final MapCodec<ModTorchBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(PARTICLE_TYPE_CODEC.forGetter(block -> block.particle), createSettingsCodec()).apply(instance, ModTorchBlock::new)
    );

    protected final SimpleParticleType particle;

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }


    public ModTorchBlock(SimpleParticleType particle, AbstractBlock.Settings settings, TorchFireState fireState, IntSupplier maxFuel) {
        super(settings, particle, fireState, maxFuel);
        this.particle = particle;
    }

    @Override
    public boolean isWall() { return false; }


}
 **/