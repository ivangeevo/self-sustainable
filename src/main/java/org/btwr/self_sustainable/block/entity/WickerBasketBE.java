package org.btwr.self_sustainable.block.entity;

import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.util.math.BlockPos;

public class WickerBasketBE extends BlockEntity implements LidOpenable {

    public WickerBasketBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WICKER_BASKET, pos, state);
    }

    @Override
    public float getAnimationProgress(float tickDelta) {
        return 0;
    }

}