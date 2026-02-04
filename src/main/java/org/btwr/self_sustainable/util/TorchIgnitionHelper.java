package org.btwr.self_sustainable.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.items.CrudeTorchBlockItem;

public final class TorchIgnitionHelper {

    private TorchIgnitionHelper() {}

    public static void lightInfiniteTorch(Item litTorch, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        if (!world.isClient) {
            // Swap unlit torch to lit
            ItemStack infiniteLitTorch = litTorch.getDefaultStack().copyWithCount(stack.getCount());

            if (hand == Hand.MAIN_HAND) {
                player.getInventory().setStack(player.getInventory().selectedSlot, infiniteLitTorch);
            }
            else {
                player.getInventory().offHand.set(0, infiniteLitTorch);
            }

            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
        }
    }

    public static void lightCrudeTorch(World world, BlockPos pos, ItemStack stack, PlayerEntity player, Hand hand) {
        if (!world.isClient) {
            // lit torch you get out of lighting
            ItemStack litTorch = CrudeTorchBlockItem.stateStack(stack, TorchFireState.LIT);

            // consume one unlit torch
            stack.decrementUnlessCreative(1, player);

            if (stack.isEmpty()) {
                // hand goes empty, replace it with the lit torch
                player.setStackInHand(hand, litTorch);
            }
            else {
                // leave the unlit stack in hand
                player.setStackInHand(hand, stack);

                // try to add the lit torch to inventory, or drop if full
                if (!player.getInventory().insertStack(litTorch)) {
                    player.dropItem(litTorch, false);
                }
            }

            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
        }
    }
}
