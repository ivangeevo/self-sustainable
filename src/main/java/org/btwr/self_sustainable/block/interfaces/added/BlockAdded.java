package org.btwr.self_sustainable.block.interfaces.added;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface BlockAdded {

    /** This a general check that returns whether the item can set this block on fire.**/
    default boolean btwr$getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$setOnFireDirectly(World world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default int btwr$getChanceOfFireSpreadingDirectlyTo(WorldAccess blockAccess, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default void btwr$checkForFireSpreadFromLocation(World world, BlockPos pos, Random random, int iSourceFireAge) {
        throw new UnsupportedOperationException();
    }

}