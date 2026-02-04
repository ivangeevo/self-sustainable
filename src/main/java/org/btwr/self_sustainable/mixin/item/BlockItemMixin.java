package org.btwr.self_sustainable.mixin.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.util.TorchIgnitionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin extends Item {

    @Shadow public abstract ActionResult useOnBlock(ItemUsageContext context);

    public BlockItemMixin(Settings settings) {
        super(settings);
    }

    // Modification to allow torches to light up from lava blocks
    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onPlace(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState stateAtPos = context.getWorld().getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack stack = context.getStack();

        // Allow normal placing over the lava if the player is sneaking for consistency with other similar behavior
        if (player != null && !player.isSneaking()) {
            if (!stateAtPos.getFluidState().isEmpty() && stateAtPos.getFluidState().isIn(FluidTags.LAVA)) {
                if (stack.isOf(ModItems.TORCH_UNLIT) || stack.isOf(ModItems.SOUL_TORCH_UNLIT)) {
                    Item litTorch = stack.isOf(ModItems.SOUL_TORCH_UNLIT) ? Items.SOUL_TORCH : Items.TORCH;
                    TorchIgnitionHelper.lightInfiniteTorch(litTorch, world, pos, player, hand, stack);
                    cir.setReturnValue(true);
                }

                if (stack.isOf(ModItems.CRUDE_TORCH_UNLIT)) {
                    TorchIgnitionHelper.lightCrudeTorch(world, pos, stack, player, hand);
                    cir.setReturnValue(true);
                }
            }
        }
    }

}