package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.ivangeevo.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder.offerOvenCooking;
import static net.minecraft.data.server.recipe.CookingRecipeJsonBuilder.*;


public class SelfSustainableRecipeProvider extends FabricRecipeProvider {

    // fcc for short
    private static final String fcc = "_from_campfire_cooking";

    private static final String foc = "_from_oven_cooking";
    // fs - from smoking
    private static final String fs = "_from_smoking";


    public SelfSustainableRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter)
    {
        this.generateVanillaRecipesOverride(exporter);
        this.generateModRecipes(exporter);
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
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
        // Cooking Recipes (we only leave campfire and smoker(until brick oven is added) as viable cooking sources now)

        // Smelting (furnace cooking recipes) removal
        /**
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_beef"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_chicken"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_mutton"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_porkchop"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_rabbit"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_salmon"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("cooked_cod"));
        removeRecipeEntry(exporter, Identifier.ofVanilla("baked_potato"));
         **/

        createCampfireCooking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.15f, 6000).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, Identifier.ofVanilla("cooked_beef" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.15f, 6000).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, Identifier.ofVanilla("cooked_chicken" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.15f, 6000).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, Identifier.ofVanilla("cooked_mutton" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.15f, 6000).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, Identifier.ofVanilla("cooked_porkchop" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.15f, 6000).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, Identifier.ofVanilla("cooked_rabbit" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.15f, 5600).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, Identifier.ofVanilla("cooked_salmon" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.15f, 5600).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, Identifier.ofVanilla("cooked_cod" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.15f, 5600).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, Identifier.ofVanilla("baked_potato" + fcc));

        createSmelting(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.25f, 2500).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, Identifier.ofVanilla("cooked_beef"));
        createSmelting(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.25f, 2500).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, Identifier.ofVanilla("cooked_chicken"));
        createSmelting(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.25f, 2500).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, Identifier.ofVanilla("cooked_mutton"));
        createSmelting(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.25f, 2500).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, Identifier.ofVanilla("cooked_porkchop"));
        createSmelting(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.25f, 2500).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, Identifier.ofVanilla("cooked_rabbit"));
        createSmelting(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.25f, 2200).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, Identifier.ofVanilla("cooked_salmon"));
        createSmelting(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.25f, 2200).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, Identifier.ofVanilla("cooked_cod"));
        createSmelting(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.25f, 2200).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, Identifier.ofVanilla("baked_potato"));

        createSmoking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35f, 1250).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, Identifier.ofVanilla("cooked_beef" + fs));
        createSmoking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35f, 1250).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, Identifier.ofVanilla("cooked_chicken" + fs));
        createSmoking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35f, 1250).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, Identifier.ofVanilla("cooked_mutton" + fs));
        createSmoking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35f, 1250).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, Identifier.ofVanilla("cooked_porkchop" + fs));
        createSmoking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35f, 1250).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, Identifier.ofVanilla("cooked_rabbit" + fs));
        createSmoking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35f, 1100).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, Identifier.ofVanilla("cooked_salmon" + fs));
        createSmoking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35f, 1100).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, Identifier.ofVanilla("cooked_cod" + fs));
        createSmoking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35f, 1100).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, Identifier.ofVanilla("baked_potato" + fs));

        // TODO: remove after Brick Ovens are added
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.FURNACE)
                .input('#', BTWRConventionalTags.Items.COBBLESTONE_CRAFTING_MATERIALS)
                .pattern("##")
                .pattern("##")
                .criterion("has_cobblestone_material", conditionsFromTag(BTWRConventionalTags.Items.COBBLESTONE_CRAFTING_MATERIALS))
                        .offerTo(exporter, Identifier.ofVanilla("furnace"));

        // Shaped Recipes
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CAMPFIRE)
                .input('S', Items.STICK)
                .pattern("SS")
                .pattern("SS")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK))
                .showNotification(true)
                .offerTo(exporter, Identifier.ofVanilla("campfire"));


        // TODO: We should re-enable this recipe removal for the furnaces after we get the Brick Oven working properly.
        //removeRecipeEntry(exporter, Identifier.ofVanilla("furnace"));
        //removeRecipeEntry(exporter, Identifier.ofVanilla("blast_furnace"));

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
        offerOvenCooking(Items.BAKED_POTATO, RecipeCategory.FOOD, Ingredient.ofItems(Items.POTATO), 0.25f, 5200).criterion("has_potato", RecipeProvider.conditionsFromItem(Items.POTATO)).offerTo(exporter, Identifier.of("self_sustainable", "baked_potato" + foc));
        offerOvenCooking(Items.COOKED_CHICKEN, RecipeCategory.FOOD, Ingredient.ofItems(Items.CHICKEN), 0.15f, 6000).criterion("has_chicken", RecipeProvider.conditionsFromItem(Items.CHICKEN)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_chicken" + foc));
        offerOvenCooking(Items.COOKED_BEEF, RecipeCategory.FOOD, Ingredient.ofItems(Items.BEEF), 0.15f, 6000).criterion("has_beef", RecipeProvider.conditionsFromItem(Items.BEEF)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_beef" + foc));
        offerOvenCooking(Items.COOKED_PORKCHOP, RecipeCategory.FOOD, Ingredient.ofItems(Items.PORKCHOP), 0.15f, 6000).criterion("has_porkchop", RecipeProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_porkchop" + foc));
        offerOvenCooking(Items.COOKED_MUTTON, RecipeCategory.FOOD, Ingredient.ofItems(Items.MUTTON), 0.15f, 6000).criterion("has_mutton", RecipeProvider.conditionsFromItem(Items.MUTTON)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_mutton" + foc));
        offerOvenCooking(Items.COOKED_RABBIT, RecipeCategory.FOOD, Ingredient.ofItems(Items.RABBIT), 0.10f, 5800).criterion("has_rabbit", RecipeProvider.conditionsFromItem(Items.RABBIT)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_rabbit" + foc));
        offerOvenCooking(Items.COOKED_COD, RecipeCategory.FOOD, Ingredient.ofItems(Items.COD), 0.10f, 5600).criterion("has_cod", RecipeProvider.conditionsFromItem(Items.COD)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_cod" + foc));
        offerOvenCooking(Items.COOKED_SALMON, RecipeCategory.FOOD, Ingredient.ofItems(Items.SALMON), 0.10f, 5600).criterion("has_salmon", RecipeProvider.conditionsFromItem(Items.SALMON)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_salmon" + foc));
        

        // Ores
        offerOvenCooking(Items.IRON_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_IRON), 0.25f, 12000).criterion("has_raw_iron", RecipeProvider.conditionsFromItem(Items.RAW_IRON)).offerTo(exporter);
        offerOvenCooking(Items.GOLD_INGOT , RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_GOLD), 0.35f, 12000).criterion("has_raw_gold", RecipeProvider.conditionsFromItem(Items.RAW_GOLD)).offerTo(exporter);
        offerOvenCooking(Items.COPPER_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_COPPER), 0.20f, 10000).criterion("has_raw_copper", RecipeProvider.conditionsFromItem(Items.RAW_COPPER)).offerTo(exporter);

    }

    private void removeRecipeEntry(RecipeExporter exporter, Identifier id)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, Items.BEDROCK).input(Items.BEDROCK)
                .criterion("has_bedrock", RecipeProvider.conditionsFromItem(Items.BEDROCK)).offerTo(exporter, id);
    }

    /**
    public static WickerWeavingRecipeJsonBuilder createWickerWeaving(ItemConvertible output, Ingredient input, int count, RecipeCategory category) {
        return new WickerWeavingRecipeJsonBuilder(category, output, count);
    }
     **/






}
