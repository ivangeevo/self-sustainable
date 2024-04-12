package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface BlockAdded
{

     boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos);

     boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos);

      boolean setOnFireDirectly(World world, BlockPos pos);

}
