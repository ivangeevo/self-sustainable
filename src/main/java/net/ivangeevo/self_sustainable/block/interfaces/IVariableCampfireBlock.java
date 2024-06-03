package net.ivangeevo.self_sustainable.block.interfaces;

import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public interface IVariableCampfireBlock
{
    BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;
    VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    VoxelShape SHAPE_WITH_SPIT = VoxelShapes.fullCube();
    IntProperty FIRE_LEVEL = IntProperty.of("fire_level", 0, 3);
    EnumProperty<CampfireState> FUEL_STATE = EnumProperty.of("fuel_state", CampfireState.class);


}
