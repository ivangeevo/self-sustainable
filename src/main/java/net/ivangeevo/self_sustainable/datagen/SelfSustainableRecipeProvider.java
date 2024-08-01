package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder;
import net.ivangeevo.self_sustainable.data.server.recipe.WickerWeavingRecipeJsonBuilder;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.item.items.ProgressiveCraftingItem;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;

import java.util.function.Consumer;


public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


    public SelfSustainableRecipeProvider(FabricDataOutput output) {
        super(output);
    }



    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter)
    {
        this.generateModRecipes(exporter);
        this.generateVanillaRecipesOverride(exporter);

    }

    private void generateModRecipes(Consumer<RecipeJsonProvider> exporter)
    {
        // Oven Recipes
        this.ovenCooking(exporter);

        // Shapeless Recipes
        this.moddedShapeless(exporter);

        // Shaped Recipes
        this.moddedShaped(exporter);

    }

    private void generateVanillaRecipesOverride(Consumer<RecipeJsonProvider> exporter)
    {

    }

    private void moddedShaped(Consumer<RecipeJsonProvider> exporter)
    {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRESTARTER_STICKS)
                .input('#', Items.STICK)
                .pattern("##")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', Items.BRICK_SLAB)
                .pattern("##")
                .pattern("##")
                .criterion("has_brick_slab", RecipeProvider.conditionsFromItem(Items.BRICK_SLAB))
                .offerTo(exporter);

        WickerWeavingRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.WICKER_WEAVING,1)
                .input('#', Items.SUGAR_CANE)
                .pattern("##")
                .pattern("##")
                .damage(ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE - 1)
                .criterion("has_sugar_cane", RecipeProvider.conditionsFromItem(Items.SUGAR_CANE))
                .offerTo(exporter);


    }


    private void moddedShapeless(Consumer<RecipeJsonProvider> exporter)
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

    private void ovenCooking(Consumer<RecipeJsonProvider> exporter)
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
        ModCookingRecipeJsonBuilder.createSmelting(Items.IRON_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_IRON), 0.25f, 12000).criterion("has_raw_iron", RecipeProvider.conditionsFromItem(Items.RAW_IRON)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createSmelting(Items.GOLD_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_GOLD), 0.35f, 12000).criterion("has_raw_gold", RecipeProvider.conditionsFromItem(Items.RAW_GOLD)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createSmelting(Items.COPPER_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_COPPER), 0.20f, 10000).criterion("has_raw_copper", RecipeProvider.conditionsFromItem(Items.RAW_COPPER)).offerTo(exporter);

    }


    public static ModCookingRecipeJsonBuilder createOvenCooking(ItemConvertible output, RecipeCategory category,Ingredient input , float experience, int cookingTime) {
        return new ModCookingRecipeJsonBuilder(category, ModCookingRecipeJsonBuilder.getSmeltingRecipeCategory(output), output, input, experience, cookingTime, OvenCookingRecipe.Serializer.INSTANCE);
    }

    public static WickerWeavingRecipeJsonBuilder createWickerWeaving(ItemConvertible output, Ingredient input, int count, RecipeCategory category) {
        return new WickerWeavingRecipeJsonBuilder(category, output, count);
    }






}
