package ivangeevo.selfsustainable.recipe.serializer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.ivangeevo.btwr.BTWRMod;
import net.ivangeevo.btwr.core.recipe.format.KnittingRecipeJsonFormat;
import net.ivangeevo.btwr.core.recipe.types.customcrafting.KnittingRecipe;
import net.minecraft.data.server.RecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class KnittingRecipeSerializer implements RecipeSerializer<KnittingRecipe> {

    // Define ExampleRecipeSerializer as a singleton by making its constructor private and exposing an instance.
    private KnittingRecipeSerializer() {
    }

    public static final KnittingRecipeSerializer INSTANCE = new KnittingRecipeSerializer();

    // This will be the "type" field in the json
    public static final Identifier ID = new Identifier(BTWRMod.MOD_ID, KnittingRecipe.Type.ID);


    public static void offerKnittingRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible output, ItemConvertible inputA, ItemConvertible inputB, ItemConvertible inputC, @Nullable String group, int outputCount) {
        ShapelessRecipeJsonBuilder.create(output, outputCount)
                .input(inputA).input(inputB).input(inputC)
                .group(group)
                .criterion(RecipeProvider.hasItem(inputA), RecipeProvider.conditionsFromItem(inputA))
                .criterion(RecipeProvider.hasItem(inputB), RecipeProvider.conditionsFromItem(inputB))
                .criterion(RecipeProvider.hasItem(inputC), RecipeProvider.conditionsFromItem(inputC))
                .offerTo(exporter);
    }


    @Override
    public KnittingRecipe read(Identifier id, PacketByteBuf buf) {
        // Make sure the read in the same order you have written!
        Ingredient inputA = Ingredient.fromPacket(buf);
        Ingredient inputB = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        return new KnittingRecipe(inputA, inputB, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, KnittingRecipe recipe) {
        recipe.getInputA().write(buf);
        recipe.getInputB().write(buf);
        buf.writeItemStack(recipe.getOutput());
    }



    @Override
    public KnittingRecipe read(Identifier id, JsonObject json) {
        KnittingRecipeJsonFormat recipeJson = new Gson().fromJson(json, KnittingRecipeJsonFormat.class);

        if (recipeJson.inputA == null || recipeJson.inputB == null || recipeJson.outputItem == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        // We'll allow to not specify the output, and default it to 1.
        if (recipeJson.outputAmount == 0) recipeJson.outputAmount = 1;


        Ingredient inputA = Ingredient.fromJson(recipeJson.inputA);
        Ingredient inputB = Ingredient.fromJson(recipeJson.inputB);
        // The json will specify the item ID. We can get the Item instance based off of that from the Item registry.
        Item outputItem = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.outputItem))
                // Validate the inputted item actually exists
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.outputItem));

        ItemStack output = new ItemStack(outputItem, recipeJson.outputAmount);

        return new KnittingRecipe(inputA, inputB, output, id);

    }
}