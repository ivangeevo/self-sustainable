package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface BlockAdded
{
    default boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return false;
    }
    default boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return false;
    }
    default boolean setOnFireDirectly(World world, BlockPos pos) {
        return false;
    }

}
