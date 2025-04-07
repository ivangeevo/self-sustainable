package net.ivangeevo.self_sustainable.block.blocks;

import com.mojang.serialization.MapCodec;
import net.ivangeevo.self_sustainable.block.entity.WickerBasketBE;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WickerBasketBlock extends BasketBlock
{
    public static final MapCodec<WickerBasketBlock> CODEC = WickerBasketBlock.createCodec(WickerBasketBlock::new);


    public WickerBasketBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WickerBasketBE(pos, state);
    }
}
