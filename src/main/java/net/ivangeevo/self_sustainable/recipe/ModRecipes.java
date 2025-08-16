package net.ivangeevo.self_sustainable.recipe;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.recipe.crafting.ShapedRecipeWithDamage;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes
{

    public static void register() {
        // Oven Cooking recipes
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Serializer.ID),
                OvenCookingRecipe.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, OvenCookingRecipe.Type.ID),
                OvenCookingRecipe.Type.INSTANCE);

        // Progressive Crafting recipes
        Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(SelfSustainableMod.MOD_ID, ShapedRecipeWithDamage.Serializer.ID),
                ShapedRecipeWithDamage.Serializer.INSTANCE);
        Registry.register(Registries.RECIPE_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, ShapedRecipeWithDamage.Type.ID),
                ShapedRecipeWithDamage.Type.INSTANCE);

    }

}
