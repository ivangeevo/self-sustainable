package net.ivangeevo.self_sustainable.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;

public record SmallPatchFeatureConfig(int tries, int xzSpread, int ySpread, RegistryEntry<PlacedFeature> feature) implements FeatureConfig
{
    public static final Codec<SmallPatchFeatureConfig> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(Codecs.POSITIVE_INT.fieldOf("tries").orElse(128)
                    .forGetter(SmallPatchFeatureConfig::tries), Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(7)
                    .forGetter(SmallPatchFeatureConfig::xzSpread), Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3)
                    .forGetter(SmallPatchFeatureConfig::ySpread), PlacedFeature.REGISTRY_CODEC.fieldOf("feature")
                    .forGetter(SmallPatchFeatureConfig::feature)).apply(instance, SmallPatchFeatureConfig::new));

    public SmallPatchFeatureConfig(int tries, int xzSpread, int ySpread, RegistryEntry<PlacedFeature> feature) {
        this.tries = tries;
        this.xzSpread = xzSpread;
        this.ySpread = ySpread;
        this.feature = feature;
    }

    public int tries() {
        return this.tries;
    }

    public int xzSpread() {
        return this.xzSpread;
    }

    public int ySpread() {
        return this.ySpread;
    }

    public RegistryEntry<PlacedFeature> feature() {
        return this.feature;
    }
}
