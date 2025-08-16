package net.ivangeevo.self_sustainable.mixin.item;

import net.ivangeevo.self_sustainable.item.component.ModComponentsTypes;
import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackAdded {

    @Shadow public abstract Item getItem();

    // Retrieve the current instance of ItemStack
    private ItemStack getThis() {
        return (ItemStack) (Object) this;
    }

    @Override
    public long getTimeOfLastUse() {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponentsTypes.LAST_USE, -1L);
    }

    @Override
    public void setTimeOfLastUse(long lTime) {
        // Set the value using the Components system
        getThis().set(ModComponentsTypes.LAST_USE, lTime);
    }

    @Override
    public float getAccumulatedChance(float fDefault) {
        // Use the Components system to retrieve the value
        return getThis().getComponents().getOrDefault(ModComponentsTypes.ACCUMULATED_CHANCE, fDefault);
    }

    @Override
    public void setAccumulatedChance(float fChance) {
        // Set the value using the Components system
        getThis().set(ModComponentsTypes.ACCUMULATED_CHANCE, fChance);
    }

    @Override
    public CustomUseAction getCustomUseAction() {
        return this.getItem().getCustomUseAction((ItemStack)(Object)this);
    }
}
