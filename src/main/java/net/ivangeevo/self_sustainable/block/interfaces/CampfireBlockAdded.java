package net.ivangeevo.self_sustainable.block.interfaces;

import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CampfireBlockAdded
{
    int getFireLevel(BlockState state);
    BlockState setFireLevel(BlockState state, int newLevel);

    void changeFireLevel( World world, BlockPos pos, int fireLevel);

    void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder);

    CampfireState getCampfireState(BlockState state);
    void relightFire(World world, BlockPos pos);
    void stopSmouldering(World world, BlockPos pos);





}