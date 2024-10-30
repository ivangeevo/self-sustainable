package net.ivangeevo.self_sustainable.item.component;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.FoodComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ModifiedFoodComponents
{
    public static final Map<FoodComponent, FoodComponent> CUSTOM_FOOD_COMPONENTS = new HashMap<>();

    static {
        // Adding custom FoodComponent configurations
        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.GOLDEN_APPLE, new FoodComponent.Builder()
                .nutrition(4)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, 0), 1.0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 2400, 0), 1.0F)
                .alwaysEdible()
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BREAD, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.DRIED_KELP, new FoodComponent.Builder()
                .nutrition(0)
                .saturationModifier(0.F)
                .statusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.CHICKEN, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PORKCHOP, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEEF, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.3F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MUTTON, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.2F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.RABBIT, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.4F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COD, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.25F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SALMON, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 1200, 2), 0.25F)
                .statusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 1), 0.1F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_CHICKEN, new FoodComponent.Builder()
                .nutrition(4)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_BEEF, new FoodComponent.Builder()
                .nutrition(4)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_PORKCHOP, new FoodComponent.Builder()
                .nutrition(4)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_MUTTON, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_RABBIT, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_COD, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKED_SALMON, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.APPLE, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MUSHROOM_STEW, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .usingConvertsTo(Items.BOWL)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.COOKIE, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.MELON_SLICE, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.CARROT, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.POTATO, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 700, 3), 0.35F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BAKED_POTATO, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PUMPKIN_PIE, new FoodComponent.Builder()
                .nutrition(2)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.RABBIT_STEW, new FoodComponent.Builder()
                .nutrition(6)
                .saturationModifier(0F)
                .usingConvertsTo(Items.BOWL)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEETROOT, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.BEETROOT_SOUP, new FoodComponent.Builder()
                .nutrition(3)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SWEET_BERRIES, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.GLOW_BERRIES, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.HONEY_BOTTLE, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.ROTTEN_FLESH, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 800, 4), 0.9F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.SPIDER_EYE, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 160, 1), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.PUFFERFISH, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 400, 1), 1.0F)
                .build());

        CUSTOM_FOOD_COMPONENTS.put(FoodComponents.POISONOUS_POTATO, new FoodComponent.Builder()
                .nutrition(1)
                .saturationModifier(0F)
                .statusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1), 1.0F)
                .build());

    }

    public static FoodComponent getCustomFoodComponent(FoodComponent original) {
        return CUSTOM_FOOD_COMPONENTS.getOrDefault(original, original);
    }
}
