package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.component.ModifiedFoodComponents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Settings.class)
public abstract class ItemSettingsMixin
{
    @Shadow public abstract <T> Settings component(ComponentType<T> type, T value);

    @Inject(method = "food", at = @At("HEAD"), cancellable = true)
    private void onSetFoodComponent(FoodComponent foodComponent, CallbackInfoReturnable<Settings> cir) {
        if (foodComponent != null)
        {
            FoodComponent customComponent = ModifiedFoodComponents.getCustomFoodComponent(foodComponent);

            cir.setReturnValue(this.component(DataComponentTypes.FOOD, customComponent));
        }
    }
}
