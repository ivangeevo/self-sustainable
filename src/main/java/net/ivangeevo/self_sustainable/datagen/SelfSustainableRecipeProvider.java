package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;


public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


    public SelfSustainableRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter)
    {
        //this.generateVanillaRecipesOverride(exporter);
        this.generateModRecipes(exporter);
    }


    private void generateModRecipes(RecipeExporter exporter)
    {
        // Oven Recipes
        this.ovenCooking(exporter);

        // Shapeless Recipes
        this.moddedShapeless(exporter);

        // Shaped Recipes
        this.moddedShaped(exporter);

    }

    private void generateVanillaRecipesOverride(RecipeExporter exporter)
    {
        // Cooking Recipes
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.15f, 6000);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.15f, 6000);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.15f, 6000);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.15f, 6000);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.15f, 6000);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.15f, 5600);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.15f, 5600);
        CookingRecipeJsonBuilder.createCampfireCooking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.15f, 5600);


        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35f, 2500);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35f, 2500);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35f, 2500);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35f, 2500);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35f, 2500);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35f, 2200);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35f, 2200);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35f, 2200);


        // Shaped Recipes


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CAMPFIRE)
                .input('S', Items.STICK)
                .pattern("SS")
                .pattern("SS")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK))
                .showNotification(true)
                .offerTo(exporter);

        removeRecipeEntry(Items.FURNACE).offerTo(exporter);
        removeRecipeEntry(Items.BLAST_FURNACE).offerTo(exporter);
        removeRecipeEntry(Items.SMOKER).offerTo(exporter);




    }

    private void moddedShaped(RecipeExporter exporter)
    {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRESTARTER_STICKS)
                .input('#', Items.STICK)
                .pattern("##")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', Items.BRICK_SLAB)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .criterion("has_bricks", RecipeProvider.conditionsFromItem(Blocks.BRICKS))
                .offerTo(exporter);

        /**
        WickerWeavingRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.WICKER_WEAVING,1)
                .input('#', Items.SUGAR_CANE)
                .pattern("##")
                .pattern("##")
                .damage(ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE - 1)
                .criterion("has_sugar_cane", RecipeProvider.conditionsFromItem(Items.SUGAR_CANE))
                .offerTo(exporter);
         **/


    }


    private void moddedShapeless(RecipeExporter exporter)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FIRESTARTER_BOW)
                .input(Items.STICK)
                .input(Items.STICK)
                .input(BTWRConventionalTags.Items.STRING_TOOL_MATERIALS)
                .criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING))
                .offerTo(exporter);

    }

    private void ovenCooking(RecipeExporter exporter)
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
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.RAW_IRON), RecipeCategory.MISC, Items.IRON_INGOT, 0.25f, 12000).criterion("has_raw_iron", RecipeProvider.conditionsFromItem(Items.RAW_IRON)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.RAW_GOLD), RecipeCategory.MISC, Items.GOLD_INGOT, 0.35f, 12000).criterion("has_raw_gold", RecipeProvider.conditionsFromItem(Items.RAW_GOLD)).offerTo(exporter);
        ModCookingRecipeJsonBuilder.createOvenCooking(Ingredient.ofItems(Items.RAW_COPPER), RecipeCategory.MISC, Items.COPPER_INGOT, 0.20f, 10000).criterion("has_raw_copper", RecipeProvider.conditionsFromItem(Items.RAW_COPPER)).offerTo(exporter);

    }

    private ShapelessRecipeJsonBuilder removeRecipeEntry(Item itemToRemove)
    {
        return ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, itemToRemove)
                .input(Items.BEDROCK).criterion("has_bedrock", RecipeProvider.conditionsFromItem(Items.BEDROCK));
    }
    public static ModCookingRecipeJsonBuilder createOvenCooking(ItemConvertible output, RecipeCategory category,Ingredient input , float experience, int cookingTime) {
        return new ModCookingRecipeJsonBuilder(category, ModCookingRecipeJsonBuilder.getRecipeCategory(output), output, input, experience, cookingTime, OvenCookingRecipe::new);
    }

    /**
    public static WickerWeavingRecipeJsonBuilder createWickerWeaving(ItemConvertible output, Ingredient input, int count, RecipeCategory category) {
        return new WickerWeavingRecipeJsonBuilder(category, output, count);
    }
     **/






}
