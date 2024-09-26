package net.ivangeevo.self_sustainable.data.server.recipe;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModCookingRecipeJsonBuilder implements CraftingRecipeJsonBuilder
{
    private final RecipeCategory category;
    private final CookingRecipeCategory cookingCategory;
    private final Item output;
    private final Ingredient input;
    private final float experience;
    private final int cookingTime;
    private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
    @Nullable
    private String group;
    private final AbstractCookingRecipe.RecipeFactory<?> recipeFactory;

    public ModCookingRecipeJsonBuilder(
            RecipeCategory category,
            CookingRecipeCategory cookingCategory,
            ItemConvertible output,
            Ingredient input,
            float experience,
            int cookingTime,
            AbstractCookingRecipe.RecipeFactory<?> recipeFactory
    ) {
        this.category = category;
        this.cookingCategory = cookingCategory;
        this.output = output.asItem();
        this.input = input;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.recipeFactory = recipeFactory;
    }

    public static <T extends AbstractCookingRecipe> ModCookingRecipeJsonBuilder create(
            Ingredient input,
            RecipeCategory category,
            ItemConvertible output,
            float experience,
            int cookingTime,
            RecipeSerializer<T> serializer,
            AbstractCookingRecipe.RecipeFactory<T> recipeFactory
    ) {
        return new ModCookingRecipeJsonBuilder(category, getCookingRecipeCategory(serializer, output), output, input, experience, cookingTime, recipeFactory);
    }

    public static ModCookingRecipeJsonBuilder createOvenCooking(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
        return new ModCookingRecipeJsonBuilder(category, getRecipeCategory(output), output, input, experience, cookingTime, OvenCookingRecipe::new);
    }


    public ModCookingRecipeJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
        this.criteria.put(string, advancementCriterion);
        return this;
    }

    public ModCookingRecipeJsonBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(RecipeExporter exporter, Identifier recipeId) {
        this.validate(recipeId);
        Advancement.Builder builder = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
        this.criteria.forEach(builder::criterion);
        AbstractCookingRecipe abstractCookingRecipe = this.recipeFactory
                .create((String)Objects.requireNonNullElse(this.group, ""), this.cookingCategory, this.input, new ItemStack(this.output), this.experience, this.cookingTime);
        exporter.accept(recipeId, abstractCookingRecipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
    }

    public static CookingRecipeCategory getRecipeCategory(ItemConvertible output) {
        if (output.asItem().getComponents().contains(DataComponentTypes.FOOD)) {
            return CookingRecipeCategory.FOOD;
        } else {
            return output.asItem() instanceof BlockItem ? CookingRecipeCategory.BLOCKS : CookingRecipeCategory.MISC;
        }
    }



    private static CookingRecipeCategory getCookingRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemConvertible output) {
        if (serializer == OvenCookingRecipe.Serializer.INSTANCE)
        {
            return getRecipeCategory(output);
        } else {
            return CookingRecipeCategory.FOOD;
        }
    }

    private void validate(Identifier recipeId) {
        if (this.criteria.isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }
}
