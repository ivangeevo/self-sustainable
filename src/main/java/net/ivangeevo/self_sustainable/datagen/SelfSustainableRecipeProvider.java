package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.ivangeevo.self_sustainable.ModItems;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SelfSustainableRecipeProvider extends FabricRecipeProvider {


    public SelfSustainableRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter)
    {

        offerThreeInputShapelessRecipe(exporter, ModItems.FIRESTARTER_BOW, Items.STICK, Items.STICK, Items.STRING,"group_btwr",1);


        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.OVEN_BRICK).input('#', Items.BRICK_SLAB)
                .pattern("##")
                .pattern("##")
                .criterion("has_brick_slab", RecipeProvider.conditionsFromItem(Items.BRICK_SLAB)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.FIRESTARTER_STICKS).input('#', Items.STICK)
                .pattern("##")
                .criterion("has_stick", RecipeProvider.conditionsFromItem(Items.STICK)).offerTo(exporter);



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


}
