package org.btwr.self_sustainable.block.interfaces.added;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface FireBlockAdded {

    default void btwr$checkForFireSpreadToOneBlockLocation(
            World world, BlockPos pos, Random rand, int sourceFireAge, boolean highHumidity, int spreadToBound)
    {
        throw new UnsupportedOperationException();
    }

    default void btwr$checkForFireSpreadFromLocation(
            World world, int i, int j, int k, Random rand, int sourceFireAge)
    {
        throw new UnsupportedOperationException();

    }

}