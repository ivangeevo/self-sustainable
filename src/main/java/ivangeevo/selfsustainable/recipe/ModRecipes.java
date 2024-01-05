package ivangeevo.selfsustainable.recipe;

import ivangeevo.selfsustainable.SelfSustainableMod;
import ivangeevo.selfsustainable.recipe.serializer.KnittingRecipeSerializer;
import ivangeevo.selfsustainable.recipe.types.customcrafting.KnittingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModRecipes {


    public static void registerRecipes() {

        // KNITTING RECIPE //
        Registry.register(Registry.RECIPE_SERIALIZER, KnittingRecipeSerializer.ID,
                KnittingRecipeSerializer.INSTANCE);
        Registry.register(Registry.RECIPE_TYPE, new Identifier(SelfSustainableMod.MOD_ID, KnittingRecipe.Type.ID), KnittingRecipe.Type.INSTANCE);

    }
}
