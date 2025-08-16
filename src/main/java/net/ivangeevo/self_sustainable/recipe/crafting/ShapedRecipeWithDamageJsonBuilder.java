package net.ivangeevo.self_sustainable.recipe.crafting;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShapedRecipeWithDamageJsonBuilder implements CraftingRecipeJsonBuilder {
	private final RecipeCategory category;
	private final Item output;
	private final int count;
	private final List<String> pattern = new ArrayList<>();
	private final Map<Character, Ingredient> inputs = new LinkedHashMap<>();
	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
	@Nullable
	private String group;
	private boolean showNotification = true;
	private int damage;

	public ShapedRecipeWithDamageJsonBuilder(RecipeCategory category, ItemConvertible output, int count) {
		this.category = category;
		this.output = output.asItem();
		this.count = count;
	}

	public static ShapedRecipeWithDamageJsonBuilder create(RecipeCategory category, ItemConvertible output) {
		return create(category, output, 1);
	}

	public static ShapedRecipeWithDamageJsonBuilder create(RecipeCategory category, ItemConvertible output, int count) {
		return new ShapedRecipeWithDamageJsonBuilder(category, output, count);
	}

	public ShapedRecipeWithDamageJsonBuilder input(Character c, TagKey<Item> tag) {
		return this.input(c, Ingredient.fromTag(tag));
	}

	public ShapedRecipeWithDamageJsonBuilder input(Character c, ItemConvertible itemProvider) {
		return this.input(c, Ingredient.ofItems(itemProvider));
	}

	public ShapedRecipeWithDamageJsonBuilder input(Character c, Ingredient ingredient) {
		if (this.inputs.containsKey(c)) {
			throw new IllegalArgumentException("Symbol '" + c + "' is already defined!");
		} else if (c == ' ') {
			throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
		}
		this.inputs.put(c, ingredient);
		return this;
	}

	public ShapedRecipeWithDamageJsonBuilder pattern(String patternStr) {
		if (!this.pattern.isEmpty() && patternStr.length() != this.pattern.getFirst().length()) {
			throw new IllegalArgumentException("Pattern must be the same width on every line!");
		}
		this.pattern.add(patternStr);
		return this;
	}

	public ShapedRecipeWithDamageJsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public ShapedRecipeWithDamageJsonBuilder group(@Nullable String group) {
		this.group = group;
		return this;
	}

	public ShapedRecipeWithDamageJsonBuilder showNotification(boolean show) {
		this.showNotification = show;
		return this;
	}

	public ShapedRecipeWithDamageJsonBuilder damage(int damage) {
		this.damage = damage;
		return this;
	}

	@Override
	public Item getOutputItem() {
		return this.output;
	}

	@Override
	public void offerTo(RecipeExporter exporter, Identifier recipeId) {
		RawShapedRecipe raw = this.validate(recipeId);
		Advancement.Builder builder = exporter.getAdvancementBuilder()
				.criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
				.rewards(AdvancementRewards.Builder.recipe(recipeId))
				.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		this.criteria.forEach(builder::criterion);

		ShapedRecipeWithDamage recipe = new ShapedRecipeWithDamage(
				Objects.requireNonNullElse(this.group, ""),
				CraftingRecipeJsonBuilder.toCraftingCategory(this.category),
				raw,
				new ItemStack(this.output, this.count),
				this.showNotification,
				this.damage
		);
		exporter.accept(recipeId, recipe, builder.build(recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

	private RawShapedRecipe validate(Identifier recipeId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + recipeId);
		}
		return RawShapedRecipe.create(this.inputs, this.pattern);
	}
}

