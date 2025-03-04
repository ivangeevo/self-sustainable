package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

public class HamperBlockEntity extends BlockEntity {

    public HamperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAMPER, pos, state);
    }


}
