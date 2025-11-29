package org.btwr.self_sustainable.block.interfaces.added;

import org.btwr.self_sustainable.block.utils.CampfireState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CampfireBlockAdded {

    default int btwr$getFireLevel(BlockState state) {
        throw new UnsupportedOperationException();
    }

    default void btwr$changeFireLevel( World world, BlockPos pos, int fireLevel) {
        throw new UnsupportedOperationException();
    }

    default void btwr$extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder) {
        throw new UnsupportedOperationException();
    }

    default void btwr$relightFire(World world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default void btwr$stopSmouldering(World world, BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    default BlockState btwr$setFireLevel(BlockState state, int newLevel) {
        throw new UnsupportedOperationException();
    }

    default CampfireState btwr$getCampfireState(BlockState state) {
        throw new UnsupportedOperationException();
    }

}