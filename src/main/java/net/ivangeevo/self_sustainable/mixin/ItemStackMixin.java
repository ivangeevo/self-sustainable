package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAdded {


    // Retrieve the current instance of ItemStack
    private ItemStack getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public long getTimeOfLastUse() {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponents.LAST_USE_COMPONENT, -1L);
    }

    @Override
    public void setTimeOfLastUse(long lTime) {
        // Set the value using the Components system
        getThis().set(ModComponents.LAST_USE_COMPONENT, lTime);
    }

    @Override
    public float getAccumulatedChance(float fDefault) {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponents.ACCUMULATED_CHANCE_COMPONENT, fDefault);
    }

    @Override
    public void setAccumulatedChance(float fChance) {
        // Set the value using the Components system
        getThis().set(ModComponents.ACCUMULATED_CHANCE_COMPONENT, fChance);
    }
}
