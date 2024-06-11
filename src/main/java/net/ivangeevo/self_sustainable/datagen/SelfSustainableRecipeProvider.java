package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;


public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


    public SelfSustainableRecipeProvider(FabricDataOutput output) {
        super(output);
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

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.WICKER_WEAVING).input('#', Items.SUGAR_CANE)
                .pattern("##")
                .pattern("##")
                .criterion("has_sugar_cane", RecipeProvider.conditionsFromItem(Items.SUGAR_CANE)).offerTo(exporter);

    }

    private void addShapelessRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FIRESTARTER_BOW).input(Items.STICK).input(Items.STICK).input(BTWRConventionalTags.Items.STRING_TOOL_MATERIALS).criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING)).offerTo(exporter);

    }

    private void addCookingRecipes()
    {

    }

    private void addOvenSmokingRecipes()
    {

    }

    private void addOvenBlastingRecipes()
    {

    }

    private void addOvenCookingRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        // Food
        
        createOvenCooking(Items.BAKED_POTATO, RecipeCategory.FOOD, Ingredient.ofItems(Items.POTATO), 0.25f, 100).criterion("has_potato", RecipeProvider.conditionsFromItem(Items.POTATO)).offerTo(exporter);
        createOvenCooking(Items.COOKED_CHICKEN, RecipeCategory.FOOD, Ingredient.ofItems(Items.CHICKEN), 0.15f, 1800).criterion("has_chicken", RecipeProvider.conditionsFromItem(Items.CHICKEN)).offerTo(exporter);
        createOvenCooking(Items.COOKED_BEEF, RecipeCategory.FOOD, Ingredient.ofItems(Items.BEEF), 0.15f, 1800).criterion("has_beef", RecipeProvider.conditionsFromItem(Items.BEEF)).offerTo(exporter);
        createOvenCooking(Items.COOKED_PORKCHOP, RecipeCategory.FOOD, Ingredient.ofItems(Items.PORKCHOP), 0.15f, 1800).criterion("has_porkchop", RecipeProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(exporter);
        createOvenCooking(Items.COOKED_MUTTON, RecipeCategory.FOOD, Ingredient.ofItems(Items.MUTTON), 0.15f, 1800).criterion("has_mutton", RecipeProvider.conditionsFromItem(Items.MUTTON)).offerTo(exporter);
        createOvenCooking(Items.COOKED_RABBIT, RecipeCategory.FOOD, Ingredient.ofItems(Items.RABBIT), 0.10f, 1600).criterion("has_rabbit", RecipeProvider.conditionsFromItem(Items.RABBIT)).offerTo(exporter);
        createOvenCooking(Items.COOKED_COD, RecipeCategory.FOOD, Ingredient.ofItems(Items.COD), 0.10f, 1600).criterion("has_cod", RecipeProvider.conditionsFromItem(Items.COD)).offerTo(exporter);
        createOvenCooking(Items.COOKED_SALMON, RecipeCategory.FOOD, Ingredient.ofItems(Items.SALMON), 0.10f, 1600).criterion("has_salmon", RecipeProvider.conditionsFromItem(Items.SALMON)).offerTo(exporter);
        

        // Ores
        ModCookingRecipeJsonBuilder.createSmelting(Items.IRON_NUGGET, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_IRON), 0.25f, 12000).criterion("has_raw_iron", RecipeProvider.conditionsFromItem(Items.RAW_IRON)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createSmelting(Items.GOLD_NUGGET, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_GOLD), 0.35f, 12000).criterion("has_raw_gold", RecipeProvider.conditionsFromItem(Items.RAW_GOLD)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createSmelting(Items.COPPER_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_COPPER), 0.20f, 10000).criterion("has_raw_copper", RecipeProvider.conditionsFromItem(Items.RAW_COPPER)).offerTo(exporter);

    }

    public static ModCookingRecipeJsonBuilder createOvenCooking(ItemConvertible output, RecipeCategory category,Ingredient input , float experience, int cookingTime) {
        return new ModCookingRecipeJsonBuilder(category, ModCookingRecipeJsonBuilder.getSmeltingRecipeCategory(output), output, input, experience, cookingTime, OvenCookingRecipe.Serializer.INSTANCE);
    }






}
