package net.ivangeevo.self_sustainable.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SmallPatchFeature
        extends Feature<SmallPatchFeatureConfig>
{
    public SmallPatchFeature(Codec<SmallPatchFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<SmallPatchFeatureConfig> context)
    {
        SmallPatchFeatureConfig patchFeatureConfig = context.getConfig();
        Random random = context.getRandom();
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();
        int i = 0;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int j = patchFeatureConfig.xzSpread() + 1;
        int k = patchFeatureConfig.ySpread() + 1;
        for (int l = 0; l < patchFeatureConfig.tries(); ++l) {
            mutable.set(blockPos, random.nextInt(j) - random.nextInt(j), random.nextInt(k) - random.nextInt(k), random.nextInt(j) - random.nextInt(j));
            if (!patchFeatureConfig.feature().value().generateUnregistered(structureWorldAccess, context.getGenerator(), random, mutable)) continue;
            ++i;
        }
        return i > 0;
    }
}
