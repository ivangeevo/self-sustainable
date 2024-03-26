package net.ivangeevo.self_sustainable.recipe;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {


    public static void registerRecipes() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Serializer.ID),
                OvenCookingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, new Identifier(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Type.ID),
                OvenCookingRecipe.Type.INSTANCE);

    }
}
