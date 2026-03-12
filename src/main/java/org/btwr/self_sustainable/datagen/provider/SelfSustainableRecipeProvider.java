package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RawShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.item.items.WickerWeavingItem;
import org.btwr.self_sustainable.recipe.KnittingRecipe;
import org.btwr.self_sustainable.recipe.WickerWeavingRecipe;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;
import org.btwr.shared_library.util.utils.IdUtils;
import org.btwr.shared_library.util.utils.RecipeUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.btwr.self_sustainable.data.server.recipe.ModCookingRecipeJsonBuilder.offerOvenCooking;
import static net.minecraft.data.server.recipe.CookingRecipeJsonBuilder.*;


public class SelfSustainableRecipeProvider extends FabricRecipeProvider implements RecipeUtils {

    // fcc for short
    private static final String fcc = "_from_campfire_cooking";

    private static final String foc = "_from_oven_cooking";
    // fs - from smoking
    private static final String fs = "_from_smoking";


    public SelfSustainableRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        this.generateVanillaRecipesOverride(exporter);
        this.generateModRecipes(exporter);
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return identifier;
    }

    private void generateModRecipes(RecipeExporter exporter) {
        // Oven Recipes
        this.createOvenCooking(exporter);

        // Shapeless Recipes
        this.moddedShapeless(exporter);

        // Shaped Recipes
        this.moddedShaped(exporter);

        // Progressive Crafting Recipes
        this.createProgressiveCrafting(exporter);
    }

    private void generateVanillaRecipesOverride(RecipeExporter exporter) {
        this.registerDisabledVanillaRecipes(exporter);

        createCampfireCooking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.15f, 6000).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, IdUtils.ofMC("cooked_beef" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.15f, 6000).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, IdUtils.ofMC("cooked_chicken" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.15f, 6000).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, IdUtils.ofMC("cooked_mutton" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.15f, 6000).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, IdUtils.ofMC("cooked_porkchop" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.15f, 6000).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, IdUtils.ofMC("cooked_rabbit" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.15f, 5600).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, IdUtils.ofMC("cooked_salmon" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.15f, 5600).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, IdUtils.ofMC("cooked_cod" + fcc));
        createCampfireCooking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.15f, 5600).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, IdUtils.ofMC("baked_potato" + fcc));

        //createSmelting(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.25f, 2500).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, IdUtils.ofMC("cooked_beef"));
        //createSmelting(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.25f, 2500).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, IdUtils.ofMC("cooked_chicken"));
        //createSmelting(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.25f, 2500).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, IdUtils.ofMC("cooked_mutton"));
        //createSmelting(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.25f, 2500).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, IdUtils.ofMC("cooked_porkchop"));
        //createSmelting(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.25f, 2500).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, IdUtils.ofMC("cooked_rabbit"));
        //createSmelting(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.25f, 2200).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, IdUtils.ofMC("cooked_salmon"));
        //createSmelting(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.25f, 2200).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, IdUtils.ofMC("cooked_cod"));
        //createSmelting(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.25f, 2200).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, IdUtils.ofMC("baked_potato"));

        //createSmoking(Ingredient.ofItems(Items.BEEF), RecipeCategory.FOOD, Items.COOKED_BEEF, 0.35f, 1250).criterion("has_beef", conditionsFromItem(Items.BEEF)).offerTo(exporter, IdUtils.ofMC("cooked_beef" + fs));
        //createSmoking(Ingredient.ofItems(Items.CHICKEN), RecipeCategory.FOOD, Items.COOKED_CHICKEN, 0.35f, 1250).criterion("has_chicken", conditionsFromItem(Items.CHICKEN)).offerTo(exporter, IdUtils.ofMC("cooked_chicken" + fs));
        //createSmoking(Ingredient.ofItems(Items.MUTTON), RecipeCategory.FOOD, Items.COOKED_MUTTON, 0.35f, 1250).criterion("has_mutton", conditionsFromItem(Items.MUTTON)).offerTo(exporter, IdUtils.ofMC("cooked_mutton" + fs));
        //createSmoking(Ingredient.ofItems(Items.PORKCHOP), RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 0.35f, 1250).criterion("has_porkchop", conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, IdUtils.ofMC("cooked_porkchop" + fs));
        //createSmoking(Ingredient.ofItems(Items.RABBIT), RecipeCategory.FOOD, Items.COOKED_RABBIT, 0.35f, 1250).criterion("has_rabbit", conditionsFromItem(Items.RABBIT)).offerTo(exporter, IdUtils.ofMC("cooked_rabbit" + fs));
        //createSmoking(Ingredient.ofItems(Items.SALMON), RecipeCategory.FOOD, Items.COOKED_SALMON, 0.35f, 1100).criterion("has_salmon", conditionsFromItem(Items.SALMON)).offerTo(exporter, IdUtils.ofMC("cooked_salmon" + fs));
        //createSmoking(Ingredient.ofItems(Items.COD), RecipeCategory.FOOD, Items.COOKED_COD, 0.35f, 1100).criterion("has_cod", conditionsFromItem(Items.COD)).offerTo(exporter, IdUtils.ofMC("cooked_cod" + fs));
        //createSmoking(Ingredient.ofItems(Items.POTATO), RecipeCategory.FOOD, Items.BAKED_POTATO, 0.35f, 1100).criterion("has_potato", conditionsFromItem(Items.POTATO)).offerTo(exporter, IdUtils.ofMC("baked_potato" + fs));

        // Shaped Recipes
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Blocks.CAMPFIRE)
                .input('S', Items.STICK)
                .pattern("SS")
                .pattern("SS")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK))
                .showNotification(true)
                .offerTo(exporter, IdUtils.ofMC("campfire"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, Items.CHEST)
                .input('#', ItemTags.PLANKS)
                .input('B', ModBlocks.HAMPER)
                .input('I', Items.IRON_INGOT)
                .pattern("###")
                .pattern("#B#")
                .pattern("#I#")
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
    }

    private void registerDisabledVanillaRecipes(RecipeExporter exporter) {
        // REMOVED BLOCKS
        disableVanilla(exporter, "furnace");
        disableVanilla(exporter, "blast_furnace");
        disableVanilla(exporter, "smoker");

        // REMOVED ITEMS
        disableVanilla(exporter, "torch");

        // Smelting (furnace cooking recipes) removal
        disableVanilla(exporter, "cooked_beef");
        disableVanilla(exporter, "cooked_chicken");
        disableVanilla(exporter, "cooked_mutton");
        disableVanilla(exporter, "cooked_porkchop");
        disableVanilla(exporter, "cooked_rabbit");
        disableVanilla(exporter, "cooked_salmon");
        disableVanilla(exporter, "cooked_cod");
        disableVanilla(exporter, "baked_potato");
        disableVanilla(exporter, "brick");

        // Smoking (smoker cooking recipes) removal
        disableVanilla(exporter, "cooked_beef" + fs);
        disableVanilla(exporter, "cooked_chicken" + fs);
        disableVanilla(exporter, "cooked_mutton" + fs);
        disableVanilla(exporter, "cooked_porkchop" + fs);
        disableVanilla(exporter, "cooked_rabbit" + fs);
        disableVanilla(exporter, "cooked_salmon" + fs);
        disableVanilla(exporter, "cooked_cod" + fs);
        disableVanilla(exporter, "baked_potato" + fs);
    }

    private void moddedShaped(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRESTARTER_STICKS)
                .input('#', Items.STICK)
                .pattern("##")
                .criterion("has_stick", conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        Item looseBrickSlab = Registries.ITEM.get(Identifier.of("tough_environment", "slab_bricks_loose"));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', looseBrickSlab)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .criterion("has_slab_bricks_loose", conditionsFromItem(looseBrickSlab))
                .offerTo(exporter, IdUtils.ofSS("oven_brick_unmortared"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', Items.BRICK_SLAB)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .criterion("has_bricks", conditionsFromItem(Blocks.BRICKS))
                .offerTo(exporter, IdUtils.ofSS("oven_brick_mortared"));

        /**
        // Register a conditional recipe for Brick ovens that loads when Tough Environment is not loaded
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', Items.BRICK_SLAB)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .criterion("has_bricks", conditionsFromItem(Blocks.BRICKS))
                .offerTo(withConditions(exporter, ResourceConditions.not(ResourceConditions.allModsLoaded("tough_environment"))), IdUtils.ofSS("oven_brick_unmortared"));

        // Register a conditional recipe for Brick ovens that requires Tough Environment to be loaded
        // Handled by a tag because I couldn't figure out how to use just an item that doesn't exist in the Registry.
        Identifier looseSlabBricksID = IdUtils.ofTE("slab_bricks_loose");
        TagKey<Item> fakeTag = TagKey.of(RegistryKeys.ITEM, looseSlabBricksID);
        Ingredient fallbackIngredient = Ingredient.fromTag(fakeTag);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK)
                .input('#', fallbackIngredient)
                .pattern("###")
                .pattern("# #")
                .pattern("###")
                .criterion("has_slab_bricks_loose", conditionsFromItem(Blocks.BRICKS))
                .offerTo(withConditions(exporter, ResourceConditions.allModsLoaded("tough_environment")), IdUtils.ofSS("oven_brick_mortared"));
         **/

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.CRUDE_TORCH_UNLIT, 4)
                .input('I', Items.STICK)
                .input('C', Items.COAL)
                .pattern("C")
                .pattern("I")
                .criterion("has_coal", conditionsFromItem(Items.COAL))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.TORCH_UNLIT, 4)
                .input('I', Items.STICK)
                .input('C', Items.COAL)
                .input('G', Items.GLOWSTONE_DUST)
                .pattern("C")
                .pattern("G")
                .pattern("I")
                .criterion("has_glowstone_dust", conditionsFromItem(Items.GLOWSTONE_DUST))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.SOUL_TORCH_UNLIT, 4)
                .input('I', Items.STICK)
                .input('C', Items.COAL)
                .input('G', Items.SOUL_SAND)
                .pattern("C")
                .pattern("G")
                .pattern("I")
                .criterion("has_soul_sand", conditionsFromItem(Items.SOUL_SAND))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.KNITTING_NEEDLES)
                .input('#', Items.STICK)
                .pattern("# ")
                .pattern(" #")
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .offerTo(exporter);

        // Wool item to wool block recipes
        for (DyeColor color : DyeColor.values()) {
            String baseId = color.getName() + "_wool";
            Item woolItem = Registries.ITEM.get(IdUtils.ofSS(baseId));
            Item woolBlock = Registries.ITEM.get(IdUtils.ofMC(baseId));

            ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, woolBlock)
                    .input('#', woolItem)
                    .pattern("##")
                    .pattern("##")
                    .criterion(hasItem(woolItem), conditionsFromItem(woolItem))
                    .offerTo(exporter, IdUtils.ofSS(baseId + "_block"));
        }

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.WICKER_BASKET)
                .input('#', ModItems.WICKER)
                .pattern("##")
                .pattern("##")
                .criterion(hasItem(ModItems.WICKER), conditionsFromItem(ModItems.WICKER))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.HAMPER)
                .input('#', ModItems.WICKER)
                .input('B', ModBlocks.WICKER_BASKET)
                .pattern("###")
                .pattern("#B#")
                .pattern("###")
                .criterion(hasItem(ModItems.WICKER), conditionsFromItem(ModItems.WICKER))
                .offerTo(exporter);
    }


    private void moddedShapeless(RecipeExporter exporter) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.FIRESTARTER_BOW)
                .input(Items.STICK)
                .input(Items.STICK)
                .input(BTWRConventionalTags.Items.STRING_TOOL_MATERIALS)
                .criterion("has_string", RecipeProvider.conditionsFromItem(Items.STRING))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.BRICK_UNFIRED)
                .input(Items.CLAY_BALL)
                .criterion("has_clay_ball", conditionsFromItem(Items.CLAY_BALL))
                .offerTo(exporter);
    }

    private void createProgressiveCrafting(RecipeExporter exporter) {
        // Knitting
        KnittingRecipe.JsonBuilder.create(RecipeCategory.MISC)
                .criterion("has_knitting_needles", conditionsFromItem(ModItems.KNITTING_NEEDLES))
                .offerTo(exporter, Identifier.of(SelfSustainableMod.MOD_ID, "knitting"));

        // Wicker weaving
        //WickerWeavingRecipe.JsonBuilder.create(RecipeCategory.MISC)
                //.criterion(hasItem(Items.SUGAR_CANE), conditionsFromItem(Items.SUGAR_CANE))
                //.offerTo(exporter);

        Identifier recipeId = IdUtils.ofSS("wicker_weaving");
        RawShapedRecipe raw = RawShapedRecipe.create(
                Map.of('S', Ingredient.ofItems(Items.SUGAR_CANE)),
                List.of("SS", "SS")
        );

        ItemStack result = new ItemStack(ModItems.WICKER_WEAVING);
        result.setDamage(WickerWeavingItem.WICKER_WEAVING_MAX_DAMAGE - 1);

        ShapedRecipe recipe = new ShapedRecipe(
                "",
                CraftingRecipeCategory.MISC,
                raw,
                result,
                true
        );

        Advancement.Builder advancement = exporter.getAdvancementBuilder()
                .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                .rewards(AdvancementRewards.Builder.recipe(recipeId))
                .criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion("has_sugar_cane", conditionsFromItem(Items.SUGAR_CANE));

        exporter.accept(recipeId, recipe, advancement.build(
                recipeId.withPrefixedPath("recipes/misc/")
        ));
    }

    private void createOvenCooking(RecipeExporter exporter) {
        // Food
        offerOvenCooking(Items.BAKED_POTATO, RecipeCategory.FOOD, Ingredient.ofItems(Items.POTATO), 0.25f, 1600).criterion("has_potato", RecipeProvider.conditionsFromItem(Items.POTATO)).offerTo(exporter, Identifier.of("self_sustainable", "baked_potato" + foc));
        offerOvenCooking(Items.COOKED_CHICKEN, RecipeCategory.FOOD, Ingredient.ofItems(Items.CHICKEN), 0.15f, 1600).criterion("has_chicken", RecipeProvider.conditionsFromItem(Items.CHICKEN)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_chicken" + foc));
        offerOvenCooking(Items.COOKED_BEEF, RecipeCategory.FOOD, Ingredient.ofItems(Items.BEEF), 0.15f, 1600).criterion("has_beef", RecipeProvider.conditionsFromItem(Items.BEEF)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_beef" + foc));
        offerOvenCooking(Items.COOKED_PORKCHOP, RecipeCategory.FOOD, Ingredient.ofItems(Items.PORKCHOP), 0.15f, 1600).criterion("has_porkchop", RecipeProvider.conditionsFromItem(Items.PORKCHOP)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_porkchop" + foc));
        offerOvenCooking(Items.COOKED_MUTTON, RecipeCategory.FOOD, Ingredient.ofItems(Items.MUTTON), 0.15f, 1600).criterion("has_mutton", RecipeProvider.conditionsFromItem(Items.MUTTON)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_mutton" + foc));
        offerOvenCooking(Items.COOKED_RABBIT, RecipeCategory.FOOD, Ingredient.ofItems(Items.RABBIT), 0.10f, 1600).criterion("has_rabbit", RecipeProvider.conditionsFromItem(Items.RABBIT)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_rabbit" + foc));
        offerOvenCooking(Items.COOKED_COD, RecipeCategory.FOOD, Ingredient.ofItems(Items.COD), 0.10f, 1600).criterion("has_cod", RecipeProvider.conditionsFromItem(Items.COD)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_cod" + foc));
        offerOvenCooking(Items.COOKED_SALMON, RecipeCategory.FOOD, Ingredient.ofItems(Items.SALMON), 0.10f, 1600).criterion("has_salmon", RecipeProvider.conditionsFromItem(Items.SALMON)).offerTo(exporter, Identifier.of("self_sustainable", "cooked_salmon" + foc));

        // Ores
        offerOvenCooking(Items.IRON_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_IRON), 0.25f, 12800).criterion("has_raw_iron", RecipeProvider.conditionsFromItem(Items.RAW_IRON)).offerTo(exporter, IdUtils.ofSS("iron_ingot" + foc));
        offerOvenCooking(Items.GOLD_INGOT , RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_GOLD), 0.35f, 12800).criterion("has_raw_gold", RecipeProvider.conditionsFromItem(Items.RAW_GOLD)).offerTo(exporter, IdUtils.ofSS("gold_ingot" + foc));
        offerOvenCooking(Items.COPPER_INGOT, RecipeCategory.MISC, Ingredient.ofItems(Items.RAW_COPPER), 0.20f, 12800).criterion("has_raw_copper", RecipeProvider.conditionsFromItem(Items.RAW_COPPER)).offerTo(exporter, IdUtils.ofSS("copper_ingot" + foc));

        // Other
        offerOvenCooking(Items.BRICK, RecipeCategory.MISC, Ingredient.ofItems(ModItems.BRICK_UNFIRED), 0.10f, 6000).criterion("has_brick_unfired", RecipeProvider.conditionsFromItem(ModItems.BRICK_UNFIRED)).offerTo(exporter, IdUtils.ofSS("brick") + foc);
    }
    
}