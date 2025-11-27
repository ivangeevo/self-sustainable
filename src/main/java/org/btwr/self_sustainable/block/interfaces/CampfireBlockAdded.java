package org.btwr.self_sustainable.block.interfaces;

import org.btwr.self_sustainable.block.utils.CampfireState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CampfireBlockAdded {

    int getFireLevel(BlockState state);

    void changeFireLevel( World world, BlockPos pos, int fireLevel);

    void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder);

    void relightFire(World world, BlockPos pos);

    void stopSmouldering(World world, BlockPos pos);

    BlockState setFireLevel(BlockState state, int newLevel);

    CampfireState getCampfireState(BlockState state);

}