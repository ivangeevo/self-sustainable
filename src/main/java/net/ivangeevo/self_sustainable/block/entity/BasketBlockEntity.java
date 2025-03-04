package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class BasketBlockEntity extends BlockEntity {

    public BasketBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WICKER_BASKET, pos, state);
    }


}
