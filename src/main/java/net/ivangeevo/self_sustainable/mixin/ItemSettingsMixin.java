package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(Item.Settings.class)
public abstract class ItemSettingsMixin {

    @Shadow public abstract Item.Settings food(FoodComponent foodComponent);

    // Map to store original FoodComponents and their custom replacements
    @Unique
    private static final Map<FoodComponent, FoodComponent> CUSTOM_FOOD_COMPONENTS = new HashMap<>();

    static {
        // Populate the map with entries, you can add more here
        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.TROPICAL_FISH, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.6F)
                .build());

        // Example for another item, let's say you wanted to modify BEEF
        // CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEEF, new FoodComponent.Builder()
        //        .hunger(8)
        //        .saturationModifier(0.8F)
        //        .build());
    }

    @Inject(method = "food", at = @At("HEAD"), cancellable = true)
    private void modifyFoodComponent(FoodComponent foodComponent, CallbackInfoReturnable<Item.Settings> cir) {
        // Check if the foodComponent has a custom replacement
        if (CUSTOM_FOOD_COMPONENTS.containsKey(foodComponent)) {
            // Get the custom FoodComponent
            FoodComponent customFoodComponent = CUSTOM_FOOD_COMPONENTS.get(foodComponent);

            // Replace the food component with the custom one
            cir.setReturnValue(this.food(customFoodComponent));
        }
    }
}
