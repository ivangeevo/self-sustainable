package net.ivangeevo.selfsustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelfSustainableRecipeProvider extends FabricRecipeProvider {
    public SelfSustainableRecipeProvider(FabricDataOutput output) {
        super(output);
    }


    // private static final List<ItemConvertible> NORMAL_LEATHERS = List.of(Items.LEATHER,ModItems.LEATHER_CUT);

   // private static final List<ItemConvertible> SCOURED_LEATHERS = List.of(ModItems.LEATHER_SCOURED,ModItems.LEATHER_SCOURED_CUT);

   // private static final List<ItemConvertible> TANNED_LEATHERS = List.of(ModItems.LEATHER_TANNED,ModItems.LEATHER_TANNED_CUT);


    public static void offerTwoInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC,output, outputCount)
                .input(input).input(input2)
                .group(group)
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    public static void offerThreeInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC,output, outputCount)
                .input(input).input(input2).input(input3)
                .group(group)
                .criterion(RecipeProvider.hasItem(input), RecipeProvider.conditionsFromItem(input))
                .offerTo(exporter, RecipeProvider.convertBetween(output, input));
    }

    public static void offerFourInputShapelessRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible input,  ItemConvertible input2, ItemConvertible input3,ItemConvertible input4, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC,output, outputCount)
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
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, output, outputCount)
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
    public void generate(Consumer<RecipeJsonProvider> exporter) {

    }
}
