package net.ivangeevo.self_sustainable.recipe.crafting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.Advancement.Builder;
import net.minecraft.advancement.AdvancementRequirements.CriterionMerger;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;

public class ShapelessRecipeWithDamageJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final int count;
	private final DefaultedList<Ingredient> inputs = DefaultedList.of();
	private final Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap<>();
	@Nullable
	private String group;
	private int damage;

	public ShapelessRecipeWithDamageJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
		this.category = category;
		this.output = output.asItem();
		this.count = count;
	}

	public static ShapelessRecipeWithDamageJsonBuilder create(RecipeCategory category, ItemConvertible output) {
		return new ShapelessRecipeWithDamageJsonBuilder(category, output, 1);
	}

	public static ShapelessRecipeWithDamageJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
		return new ShapelessRecipeWithDamageJsonBuilder(category, output, count);
	}

	public ShapelessRecipeWithDamageJsonBuilder input(TagKey<Item> tag) {
		return this.input(Ingredient.fromTag(tag));
	}

	public ShapelessRecipeWithDamageJsonBuilder input(ItemConvertible itemProvider) {
		return this.input(itemProvider, 1);
	}

	public ShapelessRecipeWithDamageJsonBuilder input(ItemConvertible itemProvider, int size) {
		for (int i = 0; i < size; i++) {
			this.input(Ingredient.ofItems(itemProvider));
		}

		return this;
	}

	public ShapelessRecipeWithDamageJsonBuilder input(Ingredient ingredient) {
		return this.input(ingredient, 1);
	}

	public ShapelessRecipeWithDamageJsonBuilder input(Ingredient ingredient, int size) {
		for (int i = 0; i < size; i++) {
			this.inputs.add(ingredient);
		}

		return this;
	}

	public ShapelessRecipeWithDamageJsonBuilder criterion(String string, AdvancementCriterion<?> advancementCriterion) {
		this.advancementBuilder.put(string, advancementCriterion);
		return this;
	}

	public ShapelessRecipeWithDamageJsonBuilder group(@Nullable String string) {
		this.group = string;
		return this;
	}

	public ShapelessRecipeWithDamageJsonBuilder damage(int damage) {
		this.damage = damage;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		this.validate(recipeId);
		Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
			.rewards(net.minecraft.advancement.AdvancementRewards.Builder.recipe(recipeId))
			.criteriaMerger(CriterionMerger.OR);
		this.advancementBuilder.forEach(builder::criterion);
		ShapelessRecipeWithDamage recipe =
				new ShapelessRecipeWithDamage(
						Objects.requireNonNullElse(this.group, ""),
						CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
						new ItemStack(this.output, this.count),
						this.inputs,
						this.damage
		);
		exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private void validate(Identifier recipeId) {
		if (this.advancementBuilder.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
	}


}
