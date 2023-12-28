package ivangeevo.selfsustainable.recipe;

import net.minecraft.recipe.BlastingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * The recipe type allows matching recipes more efficiently by only checking
 * recipes under a given type.
 *
 * @param <T> the common supertype of recipes within a recipe type
 */
public interface ModRecipeType<T extends Recipe<?>> {
    RecipeType<SmeltingRecipe> SMELTING = register("smelting");
    RecipeType<BlastingRecipe> BLASTING = register("blasting");
    RecipeType<CampfireCookingRecipe> CAMPFIRE_COOKING = register("campfire_cooking");

    RecipeType<CampfireCookingRecipe> OVEN_COOKING = register("oven_cooking");


    static <T extends Recipe<?>> RecipeType register(final String id) {
        return Registry.register(Registry.RECIPE_TYPE, (Identifier)(new Identifier(id)), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }
}
