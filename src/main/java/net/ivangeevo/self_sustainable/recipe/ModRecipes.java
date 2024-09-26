package net.ivangeevo.self_sustainable.recipe;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes
{


    public static void registerRecipes()
    {
        // Ovens
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Serializer.ID),
                OvenCookingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Type.ID),
                OvenCookingRecipe.Type.INSTANCE);


        /**
        // Progressive crafting items
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, WickerWeavingRecipe.Serializer.ID),
                WickerWeavingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, WickerWeavingRecipe.Type.ID),
                WickerWeavingRecipe.Type.INSTANCE);
         **/

    }

}
