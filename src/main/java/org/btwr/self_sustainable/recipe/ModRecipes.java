package org.btwr.self_sustainable.recipe;

import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {

    public static void register() {
        // Oven Cooking recipe
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Serializer.ID),
                OvenCookingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Type.ID),
                OvenCookingRecipe.Type.INSTANCE
        );

        // Knitting recipe
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, KnittingRecipe.Serializer.ID),
                KnittingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, KnittingRecipe.Type.ID),
                KnittingRecipe.Type.INSTANCE
        );

        // Wicker weaving recipe
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, WickerWeavingRecipe.Serializer.ID),
                WickerWeavingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, WickerWeavingRecipe.Type.ID),
                WickerWeavingRecipe.Type.INSTANCE
        );

    }

}