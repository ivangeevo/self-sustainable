/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.selfsustainable.client.gui.screen.slot;

import net.ivangeevo.selfsustainable.client.gui.screen.AbstractOvenScreenHandler;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

public class OvenFuelSlot
        extends Slot {
    private final AbstractOvenScreenHandler handler;

    public OvenFuelSlot(AbstractOvenScreenHandler handler, Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
        this.handler = handler;
    }


    @Override
    public int getMaxItemCount(ItemStack stack) {
        return OvenFuelSlot.isBucket(stack) ? 1 : super.getMaxItemCount(stack);
    }

    public static boolean isBucket(ItemStack stack) {
        return stack.isOf(Items.BUCKET);
    }
}

