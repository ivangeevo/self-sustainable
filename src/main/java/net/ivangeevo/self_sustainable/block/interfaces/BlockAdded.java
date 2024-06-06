package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
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
    default int getChanceOfFireSpreadingDirectlyTo(WorldAccess blockAccess, BlockPos pos) {
        return 0;
    }

    default void checkForFireSpreadFromLocation(World world, BlockPos pos, Random random, int iSourceFireAge) {

    }


}
