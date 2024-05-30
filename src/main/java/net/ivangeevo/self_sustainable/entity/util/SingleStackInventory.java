/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.entity.util;

import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SingleStackInventory
        extends Clearable {

    boolean isEmpty();

    /**
     * Fetches the stack currently stored at the given slot. If the slot is empty,
     * or is outside the bounds of this inventory, returns see {@link ItemStack#EMPTY}.
     */
    ItemStack getStack();

    /**
     * Removes the item from the inventory.
     *
     * @return the removed items as a stack
     */
    ItemStack removeStack();


    void setStack(ItemStack newStack);

    void markDirty();

    boolean canPlayerUse(PlayerEntity var1);

    default void onOpen(PlayerEntity player) {
    }

    default void onClose(PlayerEntity player) {
    }

    /**
     * Returns whether the given stack is a valid for the indicated slot position.
     */
    default boolean isValid(ItemStack stack) {
        return true;
    }

    default boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return true;
    }


    /**
     * Determines whether this inventory contains any of the given candidate items.
     */
    default boolean containsAny(Set<Item> items) {
        return this.containsAny((ItemStack stack) -> !stack.isEmpty() && items.contains(stack.getItem()));
    }

    default boolean containsAny(Predicate<ItemStack> predicate) {
            ItemStack itemStack = this.getStack();

        return !predicate.test(itemStack);
    }

    static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player) {
        return Inventory.canPlayerUse(blockEntity, player, 8);
    }

    static boolean canPlayerUse(BlockEntity blockEntity, PlayerEntity player, int range) {
        World world = blockEntity.getWorld();
        BlockPos blockPos = blockEntity.getPos();
        if (world == null) {
            return false;
        }
        if (world.getBlockEntity(blockPos) != blockEntity) {
            return false;
        }
        return player.squaredDistanceTo((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= (double)(range * range);
    }
}

