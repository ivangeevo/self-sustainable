package ivangeevo.selfsustainable.recipe;

import ivangeevo.selfsustainable.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class BlastingRecipe extends AbstractCookingRecipe {
    public BlastingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(ModRecipeType.BLASTING, id, group, input, output, experience, cookTime);
    }

    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.OVEN_STONE);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BLASTING;
    }
}
