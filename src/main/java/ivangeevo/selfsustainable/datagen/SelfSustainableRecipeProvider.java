package ivangeevo.selfsustainable.datagen;

import ivangeevo.selfsustainable.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


   // private static final List<ItemConvertible> NORMAL_LEATHERS = List.of(Items.LEATHER,ModItems.LEATHER_CUT);

   // private static final List<ItemConvertible> SCOURED_LEATHERS = List.of(ModItems.LEATHER_SCOURED,ModItems.LEATHER_SCOURED_CUT);

   // private static final List<ItemConvertible> TANNED_LEATHERS = List.of(ModItems.LEATHER_TANNED,ModItems.LEATHER_TANNED_CUT);

    public SelfSustainableRecipeProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    public static void offerTwoInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(output, outputCount)
                .input(input).input(input2)
                .group(group)
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    public static void offerThreeInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(output, outputCount)
                .input(input).input(input2).input(input3)
                .group(group)
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    public static void offerFourInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3,ItemConvertible input4, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(output, outputCount)
                .input(input).input(input2).input(input3).input(input4)
                .group(group)
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }
    //old & deprecated?
    //public static void offerKnittingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible inputA, WoolKnitItem inputB,WoolKnitItem inputC, @Nullable String group, int outputCount) {
        //KnittingRecipeSerializer.offerKnittingRecipe(exporter, output, inputA, inputB, inputC, group, outputCount);
    //}


    public static void offerKnittingRecipe(
            Consumer<RecipeJsonProvider> exporter,
            ItemConvertible output,
            Item knittingNeedles,
            Item woolItem1,
            Item woolItem2,
            @Nullable String group,
            int outputCount
    ) {
        ShapelessRecipeJsonBuilder.create(output, outputCount)
                .input(woolItem1)
                .input(woolItem2)
                .input(knittingNeedles)
                .group(group)
                .criterion("has_wool_item1", RecipeProvider.conditionsFromItem(woolItem1))
                .criterion("has_wool_item2", RecipeProvider.conditionsFromItem(woolItem2))
                .criterion("has_knitting_needles", RecipeProvider.conditionsFromItem(knittingNeedles))
                .offerTo(exporter, RecipeProvider.convertBetween(output, knittingNeedles));
    }










    @Override
    protected void generateRecipes(Consumer<RecipeJsonProvider> exporter) {

        /** STONE OVEN RECIPES **/

        // BRICK RECIPE
        // offerBlasting(exporter, List.of(BTWR_Items.BRICK_UNFIRED), Items.BRICK, 0.35f, 10000, "group_btwr");



        /** BRICK OVEN RECIPES **/


        // BRICK RECIPE
        // offerSmelting(exporter, List.of(ModItems.BRICK_UNFIRED),Items.BRICK,0.6f, 5000, "group_btwr");



        /** SINGLE INPUT SHAPELESS RECIPES **/

        //offerShapelessRecipe(exporter, ModItems.BRICK_UNFIRED, Items.CLAY_BALL,"group_btwr",1);

        /** TWO INPUT SHAPELESS RECIPES **/

        //offerTwoInputShapelessRecipe(exporter, ModItems.LEATHER_CUT, Items.LEATHER,Items.SHEARS,"group_btwr",2);
       // offerTwoInputShapelessRecipe(exporter, ModItems.LEATHER_TANNED_CUT, ModItems.LEATHER_TANNED,Items.SHEARS,"group_btwr",2);





        /** SPECIAL CRAFTING RECIPES **/

        //offerKnittingRecipe(exporter, ModItems.KNITTING, ModItems.KNITTING_NEEDLES,  ModItems.WHITE_WOOL, ModItems.WHITE_WOOL, "group_btwr", 1);


        /** SHAPED RECIPES **/

        //ShapedRecipeJsonBuilder.create(Items.LEATHER_HELMET).input('X', ModItems.LEATHER_CUT).pattern("XXX").pattern("X X").criterion("has_leather", conditionsFromItem(ModItems.LEATHER_CUT)).offerTo(exporter);
        //ShapedRecipeJsonBuilder.create(Items.LEATHER_CHESTPLATE).input('X', ModItems.LEATHER_CUT).pattern("X X").pattern("XXX").pattern("XXX").criterion("has_leather", conditionsFromItem(ModItems.LEATHER_CUT)).offerTo(exporter);
        //ShapedRecipeJsonBuilder.create(Items.LEATHER_LEGGINGS).input('X', ModItems.LEATHER_CUT).pattern("XXX").pattern("X X").pattern("X X").criterion("has_leather", conditionsFromItem(ModItems.LEATHER_CUT)).offerTo(exporter);
        //ShapedRecipeJsonBuilder.create(Items.LEATHER_BOOTS).input('X', ModItems.LEATHER_CUT).pattern("X X").pattern("X X").criterion("has_leather", conditionsFromItem(ModItems.LEATHER_CUT)).offerTo(exporter);


        /** ShapedRecipeJsonBuilder.create(Items.WHITE_WOOL)
                .input('X', BTWR_Items.WHITE_WOOL)
                .input('@', BTWR_Items.HEMP_FABRIC)
                .pattern("XXX")
                .pattern("X@X")
                .pattern("XXX").criterion("has_hemp_fabric", conditionsFromItem(ModItems.HEMP_FABRIC)).offerTo(exporter);
**/


    }
}
