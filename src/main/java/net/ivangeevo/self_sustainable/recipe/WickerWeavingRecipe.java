//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.ivangeevo.self_sustainable.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Iterator;

import net.ivangeevo.self_sustainable.item.items.WickerWeavingItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class WickerWeavingRecipe implements CraftingRecipe
{
    private final Identifier id;
    final String group;
    final CraftingRecipeCategory category;
    final ItemStack output;
    final DefaultedList<Ingredient> input;



    public WickerWeavingRecipe(Identifier id, String group, CraftingRecipeCategory category, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.group = group;
        this.category = category;
        this.output = output;
        this.input = input;
    }


    public Identifier getId() {
        return this.id;
    }

    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHAPELESS;
    }

    public String getGroup() {
        return this.group;
    }

    public CraftingRecipeCategory getCategory() {
        return this.category;
    }

    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return this.output;
    }

    public DefaultedList<Ingredient> getIngredients() {
        return this.input;
    }

    public boolean matches(RecipeInputInventory recipeInputInventory, World world) {
        RecipeMatcher recipeMatcher = new RecipeMatcher();
        int i = 0;

        for(int j = 0; j < recipeInputInventory.size(); ++j) {
            ItemStack itemStack = recipeInputInventory.getStack(j);
            if (!itemStack.isEmpty()) {
                ++i;
                recipeMatcher.addInput(itemStack, 1);
            }
        }

        return i == this.input.size() && recipeMatcher.match(this, null);
    }

    public ItemStack craft(RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack result = this.output.copy();

        // Customize the damage value here
        result.setDamage(WickerWeavingItem.WICKER_WEAVING_MAX_DAMAGE - 1);

        return result;
    }


    public boolean fits(int width, int height) {
        return width * height >= this.input.size();
    }


    public static class Type implements RecipeType<WickerWeavingRecipe>
    {
        public static final WickerWeavingRecipe.Type INSTANCE = new WickerWeavingRecipe.Type();
        public static final String ID = "wicker_weaving";
    }
    public static class Serializer implements RecipeSerializer<WickerWeavingRecipe>
    {
        private final RecipeFactory<?> recipeFactory;
        public static final WickerWeavingRecipe.Serializer INSTANCE = new WickerWeavingRecipe.Serializer(WickerWeavingRecipe::new);
        public static final String ID = "wicker_weaving";

        public Serializer(RecipeFactory<?> recipeFactory)
        {
            this.recipeFactory = recipeFactory;
        }


        public WickerWeavingRecipe read(Identifier identifier, JsonObject jsonObject) {
            String string = JsonHelper.getString(jsonObject, "group", "");
            CraftingRecipeCategory category = CraftingRecipeCategory.CODEC.byId(JsonHelper.getString(jsonObject, "category", null), CraftingRecipeCategory.MISC);
            DefaultedList<Ingredient> defaultedList = getIngredients(JsonHelper.getArray(jsonObject, "ingredients"));
            if (defaultedList.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else if (defaultedList.size() > 9) {
                throw new JsonParseException("Too many ingredients for shapeless recipe");
            } else {
                ItemStack itemStack = ShapedRecipe.outputFromJson(JsonHelper.getObject(jsonObject, "result"));
                return new WickerWeavingRecipe(identifier, string, category, defaultedList, itemStack);
            }
        }

        private static DefaultedList<Ingredient> getIngredients(JsonArray json) {
            DefaultedList<Ingredient> defaultedList = DefaultedList.of();

            for(int i = 0; i < json.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(json.get(i), false);
                if (!ingredient.isEmpty()) {
                    defaultedList.add(ingredient);
                }
            }

            return defaultedList;
        }

        public WickerWeavingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            String string = packetByteBuf.readString();
            CraftingRecipeCategory craftingRecipeCategory = packetByteBuf.readEnumConstant(CraftingRecipeCategory.class);
            int i = packetByteBuf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);

            defaultedList.replaceAll(ignored -> Ingredient.fromPacket(packetByteBuf));

            ItemStack itemStack = packetByteBuf.readItemStack();
            return new WickerWeavingRecipe(identifier, string, craftingRecipeCategory, defaultedList,  itemStack);
        }

        public void write(PacketByteBuf packetByteBuf, WickerWeavingRecipe recipe) {
            packetByteBuf.writeString(recipe.group);
            packetByteBuf.writeEnumConstant(recipe.category);
            packetByteBuf.writeVarInt(recipe.input.size());

            for (Ingredient ingredient : recipe.input) {
                ingredient.write(packetByteBuf);
            }

            packetByteBuf.writeItemStack(recipe.output);
        }

        public interface RecipeFactory<T extends CraftingRecipe>
        {
            T create(Identifier var1, String var2, CraftingRecipeCategory var3, DefaultedList<Ingredient> var4, ItemStack var5);
        }
    }
}
