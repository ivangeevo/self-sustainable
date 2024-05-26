package net.ivangeevo.self_sustainable.block.interfaces;

import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public interface VariableCampfireBlock
{
    BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;
    VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    VoxelShape SHAPE_WITH_SPIT = VoxelShapes.fullCube();

    IntProperty FIRE_LEVEL = CampfireBlockAdded.FIRE_LEVEL;
    EnumProperty<CampfireState> FUEL_STATE = CampfireBlockAdded.FUEL_STATE;


}
