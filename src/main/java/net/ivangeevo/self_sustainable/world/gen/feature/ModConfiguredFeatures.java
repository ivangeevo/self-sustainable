package net.ivangeevo.self_sustainable.world.gen.feature;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModConfiguredFeatures
{

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable)
    {
        ModVegetationConfiguredFeatures.bootstrap(featureRegisterable);
    }

        public static SmallPatchFeatureConfig createSmallPatchFeatureConfig(int tries, RegistryEntry<PlacedFeature> feature) {
        return new SmallPatchFeatureConfig(tries, 7, 3, feature);
    }
}
