package net.ivangeevo.self_sustainable.item.component;

import btwr.core.item.BTWR_Items;
import com.bwt.items.BwtItems;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import org.ivangeevo.vegehenna.item.ModItems;

public class FoodComponentModifier {

   private static final boolean isBTWRDSLoaded = FabricLoader.getInstance().isModLoaded("btwr_ds");

    /**
     * Registers a listener to modify the food components of food items.
     */
    public static void register() {
        // BTWR: DS does its own food modifications, so don't apply if it's present
        if (!isBTWRDSLoaded) {
            DefaultItemComponentEvents.MODIFY.register(FoodComponentModifier::modifyFoodComponents);
        }
    }

    // Method to modify components
    private static void modifyFoodComponents(DefaultItemComponentEvents.ModifyContext context) {
        context.modify(Items.GOLDEN_APPLE, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addRegenerationEffect(100, 0), 1F).statusEffect(addAbsorptionEffect(2400, 0), 1F).alwaysEdible().build()));
        context.modify(Items.BREAD, builder -> modifyEntry(builder, replaceWith(3).build()));
        context.modify(Items.DRIED_KELP, builder -> modifyEntry(builder, replaceWith(0).build()));
        context.modify(Items.CHICKEN, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 2), 0.1F).build()));
        context.modify(Items.PORKCHOP, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 2), 0.1F).build()));
        context.modify(Items.BEEF, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 2), 0.1F).build()));
        context.modify(Items.MUTTON, builder -> modifyEntry(builder, replaceWith(2).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
        context.modify(Items.RABBIT, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
        context.modify(Items.COD, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
        context.modify(Items.SALMON, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addHungerEffect(1200, 2), 0.25F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
        context.modify(Items.COOKED_CHICKEN, builder -> modifyEntry(builder, replaceWith(4).build()));
        context.modify(Items.COOKED_BEEF, builder -> modifyEntry(builder, replaceWith(4).build()));
        context.modify(Items.COOKED_PORKCHOP, builder -> modifyEntry(builder, replaceWith(5).build()));
        context.modify(Items.COOKED_MUTTON, builder -> modifyEntry(builder, replaceWith(4).build()));
        context.modify(Items.COOKED_RABBIT, builder -> modifyEntry(builder, replaceWith(3).build()));
        context.modify(Items.COOKED_COD, builder -> modifyEntry(builder, replaceWith(2).build()));
        context.modify(Items.COOKED_SALMON, builder -> modifyEntry(builder, replaceWith(2).build()));
        context.modify(Items.APPLE, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.MUSHROOM_STEW, builder -> modifyEntry(builder, replaceWith(2).build()));
        context.modify(Items.COOKIE, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.MELON_SLICE, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.CARROT, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.POTATO, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addHungerEffect(700, 3), 0.35F).build()));
        context.modify(Items.BAKED_POTATO, builder -> modifyEntry(builder, replaceWith(2).build()));
        context.modify(Items.PUMPKIN_PIE, builder -> modifyEntry(builder, replaceWith(2).build()));
        context.modify(Items.RABBIT_STEW, builder -> modifyEntry(builder, replaceWith(6).build()));
        context.modify(Items.BEETROOT, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.BEETROOT_SOUP, builder -> modifyEntry(builder, replaceWith(3).build()));
        context.modify(Items.SWEET_BERRIES, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.GLOW_BERRIES, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.HONEY_BOTTLE, builder -> modifyEntry(builder, replaceWith(1).build()));
        context.modify(Items.ROTTEN_FLESH, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(600, 4), 0.8F).build()));
        context.modify(Items.SPIDER_EYE, builder -> modifyEntry(builder, replaceWith(2).statusEffect(addPoisonEffect(100, 0), 1.0F).build()));
        context.modify(Items.PUFFERFISH, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addPoisonEffect(400, 1), 1.0F).build()));
        context.modify(Items.POISONOUS_POTATO, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addPoisonEffect(200, 1), 1.0F).build()));
        context.modify(Items.CHORUS_FRUIT, builder -> modifyEntry(builder, replaceWith(1).build()));

        if (FabricLoader.getInstance().isModLoaded("btwr")) {
            context.modify(BTWR_Items.CHOWDER, builder -> modifyEntry(builder, replaceWith(5).build()));
            context.modify(BTWR_Items.EGG_SCRAMBLED_RAW, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(600, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
            context.modify(BTWR_Items.MUSHROOM_OMELETTE_RAW, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(600, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
            context.modify(BTWR_Items.RAW_KEBAB, builder -> modifyEntry(builder, replaceWith(6).statusEffect(addHungerEffect(600, 2), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
            context.modify(BTWR_Items.EGG_SCRAMBLED_COOKED, builder -> modifyEntry(builder, replaceWith(4).build()));
            context.modify(BTWR_Items.MUSHROOM_OMELETTE_COOKED, builder -> modifyEntry(builder, replaceWith(4).build()));
            context.modify(BTWR_Items.SANDWICH, builder -> modifyEntry(builder, replaceWith(5).build()));
            context.modify(BTWR_Items.HAM_AND_EGGS, builder -> modifyEntry(builder, replaceWith(6).build()));
            context.modify(BTWR_Items.STEAK_AND_POTATOES, builder -> modifyEntry(builder, replaceWith(6).build()));
            context.modify(BTWR_Items.COOKED_KEBAB, builder -> modifyEntry(builder, replaceWith(8).build()));
            context.modify(BTWR_Items.STEAK_DINNER, builder -> modifyEntry(builder, replaceWith(8).build()));
            context.modify(BTWR_Items.PORK_DINNER, builder -> modifyEntry(builder, replaceWith(8).build()));
            context.modify(BTWR_Items.WOLF_DINNER, builder -> modifyEntry(builder, replaceWith(8).build()));
            context.modify(BTWR_Items.CHICKEN_SOUP, builder -> modifyEntry(builder, replaceWith(8).build()));
            context.modify(BTWR_Items.HEARTY_STEW, builder -> modifyEntry(builder, createStew(10).build()));
            context.modify(BTWR_Items.CREEPER_OYSTERS, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addPoisonEffect(100, 0), 1.0F).build()));
            context.modify(BTWR_Items.BEAST_LIVER_RAW, builder -> modifyEntry(builder, replaceWith(5).statusEffect(addHungerEffect(600, 1), 0.3F).statusEffect(addSlownessEffect(1000, 1), 0.1F).build()));
            context.modify(BTWR_Items.BEAST_LIVER_COOKED, builder -> modifyEntry(builder, replaceWith(6).build()));
        }

        if (FabricLoader.getInstance().isModLoaded("vegehenna")) {
            context.modify(ModItems.BOILED_POTATO, builder -> modifyEntry(builder, replaceWith(1).build()));
            context.modify(ModItems.COOKED_CARROT, builder -> modifyEntry(builder, replaceWith(2).build()));
            context.modify(ModItems.CHOCOLATE, builder -> modifyEntry(builder, replaceWith(2).build()));
            context.modify(ModItems.CHOCOLATE_MILK, builder -> modifyEntry(builder, replaceWith(3).build()));

        }

        if (FabricLoader.getInstance().isModLoaded("bwt")) {
            context.modify(BwtItems.rawEggItem, builder -> modifyEntry(builder, replaceWith(1).statusEffect(addHungerEffect(600, 2), 0.3F).build()));
            context.modify(BwtItems.wolfChopItem, builder -> modifyEntry(builder, replaceWith(3).statusEffect(addHungerEffect(1200, 2), 0.3F).statusEffect(addSlownessEffect(1000, 2), 0.1F).build()));
            context.modify(BwtItems.friedEggItem, builder -> modifyEntry(builder, replaceWith(3).build()));
            context.modify(BwtItems.poachedEggItem, builder -> modifyEntry(builder, replaceWith(3).build()));
            context.modify(BwtItems.cookedWolfChopItem, builder -> modifyEntry(builder, replaceWith(5).build()));
            context.modify(BwtItems.donutItem, builder -> modifyEntry(builder, new FoodComponent.Builder().nutrition(1).snack().saturationModifier(0f).build()));
        }
    }

    // Directly modify the builder with access widening the put method (it was reflection before)
    private static void modifyEntry(ComponentMap.Builder builder, FoodComponent foodComponent) {
        builder.put(DataComponentTypes.FOOD, foodComponent);
    }

    private static FoodComponent.Builder replaceWith(int hunger) {
        return new FoodComponent.Builder().nutrition(hunger).saturationModifier(0f);
    }

    private static FoodComponent.Builder createStew(int hunger) {
        return (new FoodComponent.Builder()).nutrition(hunger).saturationModifier(0f).usingConvertsTo(Items.BOWL);
    }

    private static StatusEffectInstance addAbsorptionEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.ABSORPTION, dur, amp, false, false, false);
    }

    private static StatusEffectInstance addRegenerationEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.REGENERATION, dur, amp, false, false, false);
    }

    private static StatusEffectInstance addSlownessEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.SLOWNESS, dur, amp, false, false, false);
    }

    private static StatusEffectInstance addWeaknessEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.WEAKNESS, dur, amp, false, false, false);
    }

    private static StatusEffectInstance addHungerEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.HUNGER, dur, amp, false, false, false);
    }

    private static StatusEffectInstance addPoisonEffect(int dur, int amp) {
        return new StatusEffectInstance(StatusEffects.POISON, dur, amp, false, false, false);
    }

}