package org.btwr.self_sustainable.block.entity;

import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class HamperBlockEntity extends BlockEntity {

    public HamperBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAMPER, pos, state);
    }

}