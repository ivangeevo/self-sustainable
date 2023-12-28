/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package ivangeevo.selfsustainable.client.gui.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;

public class OvenScreenHandler
        extends AbstractOvenScreenHandler {
    public OvenScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory);
    }

    public OvenScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlerType.FURNACE, RecipeType.SMELTING, RecipeBookCategory.FURNACE, syncId, playerInventory, inventory, propertyDelegate);
    }
}

