package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.ivangeevo.self_sustainable.block.interfaces.TorchBlockAdded;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VerticallyAttachableBlockItem.class)
public abstract class VerticallyAttachableBlockItemMixin extends BlockItem implements TorchBlockAdded, BlockEntityProvider
{

    public VerticallyAttachableBlockItemMixin(Block block, Settings settings)
    {
        super(block, settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectedSetState(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection, CallbackInfo ci)
    {
        wallBlock.getStateManager().getDefaultState().with(LIT, false);
    }



    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        if (state.isOf(Blocks.WALL_TORCH))
        {
            return new TorchBlockEntity(pos, state);
        }

        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        // Add the ticker methods for each case of the block entity's logical side. I think lol...
        if (world.isClient)
        { return checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::clientTick); }
        else
        { return checkType(type, ModBlockEntities.TORCH, TorchBlockEntity::serverTick); }

    }

    @Unique
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }





}
