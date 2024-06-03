package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface FireBlockAdded
{
    void checkForFireSpreadToOneBlockLocation(World world, BlockPos pos, Random rand, int iSourceFireAge, boolean bHighHumidity, int iSpreadTopBound);
    void checkForFireSpreadFromLocation(World world, int i, int j, int k, java.util.Random rand, int iSourceFireAge);

}
