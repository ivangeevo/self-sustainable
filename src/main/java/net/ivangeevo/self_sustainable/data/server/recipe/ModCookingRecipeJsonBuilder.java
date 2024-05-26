/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.data.server.recipe;

import com.google.gson.JsonObject;
import java.util.function.Consumer;

import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModCookingRecipeJsonBuilder
        implements CraftingRecipeJsonBuilder {
    private final RecipeCategory category;
    private final CookingRecipeCategory cookingCategory;
    private final Item output;
    private final Ingredient input;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancementBuilder = Advancement.Builder.createUntelemetered();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

    public ModCookingRecipeJsonBuilder(RecipeCategory category, CookingRecipeCategory cookingCategory, ItemConvertible output, Ingredient input, float experience, int cookingTime, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        this.category = category;
        this.cookingCategory = cookingCategory;
        this.output = output.asItem();
        this.input = input;
        this.experience = experience;
        this.cookingTime = cookingTime;
        this.serializer = serializer;
    }

    public static ModCookingRecipeJsonBuilder create(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
        return new ModCookingRecipeJsonBuilder(category, ModCookingRecipeJsonBuilder.getCookingRecipeCategory(serializer, output), output, input, experience, cookingTime, serializer);
    }



    public static ModCookingRecipeJsonBuilder createOvenCooking(Ingredient input, RecipeCategory category, ItemConvertible output, float experience, int cookingTime) {
        return new ModCookingRecipeJsonBuilder(category, getSmeltingRecipeCategory(output), output, input, experience, cookingTime, OvenCookingRecipe.Serializer.INSTANCE);
    }



    @Override
    public ModCookingRecipeJsonBuilder criterion(String string, CriterionConditions criterionConditions) {
        this.advancementBuilder.criterion(string, criterionConditions);
        return this;
    }

    @Override
    public ModCookingRecipeJsonBuilder group(@Nullable String string) {
        this.group = string;
        return this;
    }

    @Override
    public Item getOutputItem() {
        return this.output;
    }

    @Override
    public void offerTo(Consumer<RecipeJsonProvider> exporter, Identifier recipeId) {
        this.validate(recipeId);
        this.advancementBuilder.parent(ROOT).criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId)).rewards(AdvancementRewards.Builder.recipe(recipeId)).criteriaMerger(CriterionMerger.OR);
        exporter.accept(new CookingRecipeJsonProvider(recipeId, this.group == null ? "" : this.group, this.cookingCategory, this.input, this.output, this.experience, this.cookingTime, this.advancementBuilder, recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/"), this.serializer));
    }

    public static CookingRecipeCategory getSmeltingRecipeCategory(ItemConvertible output) {
        if (output.asItem().isFood()) {
            return CookingRecipeCategory.FOOD;
        }
        if (output.asItem() instanceof BlockItem) {
            return CookingRecipeCategory.BLOCKS;
        }
        return CookingRecipeCategory.MISC;
    }

    private static CookingRecipeCategory getBlastingRecipeCategory(ItemConvertible output) {
        if (output.asItem() instanceof BlockItem) {
            return CookingRecipeCategory.BLOCKS;
        }
        return CookingRecipeCategory.MISC;
    }

    private static CookingRecipeCategory getCookingRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> serializer, ItemConvertible output) {
        if (serializer == RecipeSerializer.SMELTING || serializer == OvenCookingRecipe.Serializer.INSTANCE) {
            return ModCookingRecipeJsonBuilder.getSmeltingRecipeCategory(output);
        }
        if (serializer == RecipeSerializer.BLASTING) {
            return ModCookingRecipeJsonBuilder.getBlastingRecipeCategory(output);
        }
        if (serializer == RecipeSerializer.SMOKING || serializer == RecipeSerializer.CAMPFIRE_COOKING) {
            return CookingRecipeCategory.FOOD;
        }
        throw new IllegalStateException("Unknown cooking recipe type");
    }

    private void validate(Identifier recipeId) {
        if (this.advancementBuilder.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + recipeId);
        }
    }



    static class CookingRecipeJsonProvider
            implements RecipeJsonProvider {
        private final Identifier recipeId;
        private final String group;
        private final CookingRecipeCategory category;
        private final Ingredient input;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancementBuilder;
        private final Identifier advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public CookingRecipeJsonProvider(Identifier recipeId, String group, CookingRecipeCategory category, Ingredient input, Item result, float experience, int cookingTime, Advancement.Builder advancementBuilder, Identifier advancementId, RecipeSerializer<? extends AbstractCookingRecipe> serializer) {
            this.recipeId = recipeId;
            this.group = group;
            this.category = category;
            this.input = input;
            this.result = result;
            this.experience = experience;
            this.cookingTime = cookingTime;
            this.advancementBuilder = advancementBuilder;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.addProperty("category", this.category.asString());
            json.add("ingredient", this.input.toJson());
            json.addProperty("result", Registries.ITEM.getId(this.result).toString());
            json.addProperty("experience", Float.valueOf(this.experience));
            json.addProperty("cookingtime", this.cookingTime);
        }

        @Override
        public RecipeSerializer<?> getSerializer() {
            return this.serializer;
        }

        @Override
        public Identifier getRecipeId() {
            return this.recipeId;
        }

        @Override
        @Nullable
        public JsonObject toAdvancementJson() {
            return this.advancementBuilder.toJson();
        }

        @Override
        @Nullable
        public Identifier getAdvancementId() {
            return this.advancementId;
        }
    }
}

