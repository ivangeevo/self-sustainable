package org.btwr.self_sustainable.mixin.entity;

import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.TorchFuelComponent;
import org.btwr.self_sustainable.item.items.CrudeTorchBlockItem;
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
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStack();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getWorld()Lnet/minecraft/world/World;", ordinal = 5))
    private void onTick(CallbackInfo ci) {
        ItemStack thisStack = this.getStack();

        if (!isFuelHavingTorch(thisStack)) return;

        TorchFuelComponent fuelComponent = thisStack.getOrDefault(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent());
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
        return stack.getItem() instanceof CrudeTorchBlockItem &&
                stack.isOf(ModItems.CRUDE_TORCH_LIT) || stack.isOf(ModItems.CRUDE_TORCH_SMOULDER);
    }

}