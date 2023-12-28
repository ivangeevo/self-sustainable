package ivangeevo.selfsustainable.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class SmeltingRecipe extends AbstractCookingRecipe {
    public SmeltingRecipe(Identifier id, String group, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(BTWR_RecipeType.SMELTING, id, group, input, output, experience, cookTime);
    }

    public ItemStack createIcon() {
        return new ItemStack(Blocks.FURNACE);
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMELTING;
    }
}
