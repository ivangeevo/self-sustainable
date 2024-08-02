package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
        // Adding custom FoodComponent configurations
        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.GOLDEN_APPLE, new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.2F)
                .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0), 1.0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F)
                .alwaysEdible()
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BREAD, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.02F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.DRIED_KELP, new FoodComponent.Builder()
                .hunger(0)
                .saturationModifier(0.0025F)
                .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.CHICKEN, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.01F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PORKCHOP, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.01F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEEF, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.01F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.3F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MUTTON, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.008F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.2F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.RABBIT, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.005F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COD, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.003F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.25F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SALMON, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.003F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.25F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_CHICKEN, new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.022F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_BEEF, new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.02F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_PORKCHOP, new FoodComponent.Builder()
                .hunger(4)
                .saturationModifier(0.02F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_MUTTON, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.02F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_RABBIT, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.012F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_COD, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.008F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_SALMON, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.008F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.APPLE, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.005F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MUSHROOM_STEW, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.02F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKIE, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.003F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MELON_SLICE, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.004F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.CARROT, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.005F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.POTATO, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.0015F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 700, 3), 0.35F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BAKED_POTATO, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.005F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PUMPKIN_PIE, new FoodComponent.Builder()
                .hunger(2)
                .saturationModifier(0.01F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.RABBIT_STEW, new FoodComponent.Builder()
                .hunger(6)
                .saturationModifier(0.01F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEETROOT, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.0015F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEETROOT_SOUP, new FoodComponent.Builder()
                .hunger(3)
                .saturationModifier(0.004F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SWEET_BERRIES, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.0015F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.GLOW_BERRIES, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.0015F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.HONEY_BOTTLE, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.0015F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.ROTTEN_FLESH, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.001F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 800, 4), 0.9F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SPIDER_EYE, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.001F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 1), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PUFFERFISH, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.001F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 400, 1), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.POISONOUS_POTATO, new FoodComponent.Builder()
                .hunger(1)
                .saturationModifier(0.001F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1), 1.0F)
                .build());
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
