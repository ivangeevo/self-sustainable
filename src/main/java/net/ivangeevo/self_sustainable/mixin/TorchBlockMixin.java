package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends Block
{
    public TorchBlockMixin(Settings settings) {
        super(settings);
    }



    @Unique
    private boolean isTorch(WorldView world, BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        boolean checkWorldNormal = blockState.isOf(Blocks.TORCH);
        boolean checkWorldWallNormal = blockState.isOf(Blocks.WALL_TORCH);
        boolean checkWorldSoul = blockState.isOf(Blocks.SOUL_TORCH);
        boolean checkWorldWallSoul = blockState.isOf(Blocks.SOUL_WALL_TORCH);

        return checkWorldNormal || checkWorldWallNormal || checkWorldSoul || checkWorldWallSoul;
    }

    @Unique
    private boolean isCampfire(WorldView world, BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        boolean checkWorldNormal = blockState.isOf(Blocks.CAMPFIRE);
        boolean checkWorldSoul = blockState.isOf(Blocks.SOUL_CAMPFIRE);

        return checkWorldNormal || checkWorldSoul;
    }


}
