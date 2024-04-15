package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.ModItems;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


    public SelfSustainableRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    public static void offerTwoInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input, ItemConvertible input2, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
                .input(input).input(input2)
                .group(group)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, convertBetween(output, input));
    }
    public static void offerThreeInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
                .input(input).input(input2).input(input3)
                .group(group)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter, convertBetween(output, input));
    }
    public static void offerFourInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3,ItemConvertible input4, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
                .input(input).input(input2).input(input3).input(input4)
                .group(group)
                .criterion(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter,  convertBetween(output, input));
    }


    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter)
    {
        /** Furnace temporary Recipes **/
        //addFurnaceCookingRecipes(exporter);


        /** Oven Recipes **/
        this.addOvenCookingRecipes(exporter);

        /** Shapeless Recipes **/
        this.addShapelessRecipes(exporter);

        /** Shaped Recipes **/
        this.addShapedRecipes(exporter);

    }

    private void addShapedRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRESTARTER_STICKS).input('#', Items.STICK)
                .pattern("##")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK).input('#', Items.BRICK_SLAB)
                .pattern("##")
                .pattern("##")
                .criterion("has_brick_slab", RecipeProvider.conditionsFromItem(Items.BRICK_SLAB)).offerTo(exporter);

    }

    private void addShapelessRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        offerThreeInputShapelessRecipe(exporter, ModItems.FIRESTARTER_BOW, Items.STICK, Items.STICK, Items.STRING,"group_btwr",1);
    }

    private void addOvenCookingRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        // Food
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.BAKED_POTATO), RecipeCategory.FOOD, Items.POTATO, 0.25f, 100);

        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_CHICKEN), RecipeCategory.FOOD, Items.CHICKEN, 0.15f, 1800);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_BEEF), RecipeCategory.FOOD, Items.BEEF, 0.15f, 1800);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_PORKCHOP), RecipeCategory.FOOD, Items.PORKCHOP, 0.15f, 1800);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_MUTTON), RecipeCategory.FOOD, Items.MUTTON, 0.15f, 1800);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_RABBIT), RecipeCategory.FOOD, Items.RABBIT, 0.10f, 1600);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_COD), RecipeCategory.FOOD, Items.COD, 0.10f, 1600);
        ModCookingRecipeJsonBuilder.createOvenSmelting(Ingredient.ofItems(Items.COOKED_SALMON), RecipeCategory.FOOD, Items.SALMON, 0.10f, 1600);

        // Ores
        ModCookingRecipeJsonBuilder.create(Ingredient.ofItems(Items.IRON_NUGGET), RecipeCategory.MISC, Items.RAW_IRON, 0.25f, 12000, OvenCookingRecipe.Serializer.INSTANCE);

    }

    private void addFurnaceCookingRecipes(Consumer<RecipeJsonProvider> exporter)
    {

        // Ores
        VanillaRecipeProvider.offerSmelting(exporter, List.of(Items.RAW_IRON), RecipeCategory.MISC, Items.IRON_NUGGET, 0.25f, 120, "group_btwr");

    }



}
