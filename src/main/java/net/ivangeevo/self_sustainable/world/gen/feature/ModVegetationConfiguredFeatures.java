package net.ivangeevo.self_sustainable.world.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

import java.util.List;

public class ModVegetationConfiguredFeatures
{

    public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_CUSTOM_BERRY_BUSH = ConfiguredFeatures.of("patch_custom_berry_bush");

    private static SmallPatchFeatureConfig createSmallPatchFeatureConfig(BlockStateProvider block, int tries)
    {
        return ModConfiguredFeatures.createSmallPatchFeatureConfig(tries, PlacedFeatures.createEntry(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(block)));
    }

    public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable)
    {
        ConfiguredFeatures.register(featureRegisterable, PATCH_CUSTOM_BERRY_BUSH, Feature.RANDOM_PATCH, ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, 3))), List.of(Blocks.GRASS_BLOCK)));

    }
}
