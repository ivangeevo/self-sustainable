package net.ivangeevo.self_sustainable.block.interfaces;

import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CampfireBlockAdded
{

    IntProperty FIRE_LEVEL = IntProperty.of("fire_level", 0, 3);

    EnumProperty<CampfireState> FUEL_STATE = EnumProperty.of("fuel_state", CampfireState.class);

    int getFireLevel(BlockState state);
    BlockState setFireLevel(BlockState state, int newLevel);

    void changeFireLevel(BlockState state, int newFireLevel);

    void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder);

    CampfireState getFuelState(BlockState state);


    void relightFire(BlockState state);





}