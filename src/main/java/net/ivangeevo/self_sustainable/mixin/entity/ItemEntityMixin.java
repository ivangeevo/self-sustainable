package net.ivangeevo.self_sustainable.mixin.entity;

import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.item.component.TorchFuelComponent;
import net.ivangeevo.self_sustainable.item.items.CrudeTorchItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity
{
    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getWorld()Lnet/minecraft/world/World;", ordinal = 5))
    private void onTick(CallbackInfo ci) {
        ItemStack thisStack = this.getStack();
        if (!(thisStack.getItem() instanceof CrudeTorchItem) && !isFuelHavingTorch(thisStack)) return;
        TorchFuelComponent fuelComponent = thisStack.getOrDefault(ModComponents.TORCH_FUEL_COMPONENT, new TorchFuelComponent());
        assert fuelComponent != null;
        // keep decrementing the fuel for the dropped torch
        if (this.isOnGround() && this.isAlive()) {
            fuelComponent.decrement();
        }
        // remove the entity when fuel runs out
        if (fuelComponent.getFuel() == 0) {
            this.discard();
        }

    }

    @Unique
    private boolean isFuelHavingTorch(ItemStack stack) {
        return stack.isOf(ModItems.CRUDE_TORCH_LIT) || stack.isOf(ModItems.CRUDE_TORCH_SMOULDER);
    }

}
