package org.btwr.self_sustainable.mixin.added;

import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.interfaces.added.ItemStackAdded;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackAddedMixin implements ItemStackAdded {

    // Retrieve the current instance of ItemStack
    private ItemStack getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public long btwr$getTimeOfLastUse() {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponentsTypes.LAST_USE, -1L);
    }

    @Override
    public void btwr$setTimeOfLastUse(long lTime) {
        // Set the value using the Components system
        getThis().set(ModComponentsTypes.LAST_USE, lTime);
    }

    @Override
    public float btwr$getAccumulatedChance(float fDefault) {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponentsTypes.ACCUMULATED_CHANCE, fDefault);
    }

    @Override
    public void btwr$setAccumulatedChance(float fChance) {
        // Set the value using the Components system
        getThis().set(ModComponentsTypes.ACCUMULATED_CHANCE, fChance);
    }

}