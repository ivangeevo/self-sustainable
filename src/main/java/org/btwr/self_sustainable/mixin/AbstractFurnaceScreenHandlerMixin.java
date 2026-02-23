package org.btwr.self_sustainable.mixin;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceScreenHandler.class)
public abstract class AbstractFurnaceScreenHandlerMixin {

    @Inject(method = "<init>(Lnet/minecraft/screen/ScreenHandlerType;Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/recipe/book/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/screen/PropertyDelegate;)V",
            at = @At("TAIL"))
    private void modifySlotLogic(ScreenHandlerType<?> type, RecipeType<? extends AbstractCookingRecipe> recipeType, RecipeBookCategory category, int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate, CallbackInfo ci) {
        // Access the 'this' object, which is the AbstractFurnaceScreenHandler instance
        AbstractFurnaceScreenHandler screenHandler = (AbstractFurnaceScreenHandler) (Object) this;

        // Modify the input slot (index 0) directly
        Slot inputSlot = screenHandler.slots.getFirst();  // The slot where the item is put to be cooked
        if (inputSlot != null) {
            // Replacing the original input slot with a new slot that enforces the max stack size
            screenHandler.slots.set(0, new Slot(inputSlot.inventory, inputSlot.getIndex(), inputSlot.x, inputSlot.y) {
                @Override
                public int getMaxItemCount(ItemStack stack) {
                    return 1; // Enforcing a custom max stack size of 1
                }
            });
        }
    }

}