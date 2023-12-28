package ivangeevo.selfsustainable.recipe;

import com.google.gson.JsonObject;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
    public CampfireCookingRecipe(Identifier id, String group, Ingredient input, ItemStack result, float experience, int cookTime) {
        super(ModRecipeType.CAMPFIRE_COOKING, id, group, input, result, experience, cookTime);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return null;
    }

    @Override
    public ItemStack getOutput() {
        return null;
    }

    public ItemStack createIcon() {
        return new ItemStack(Blocks.CAMPFIRE);
    }


    public static class Type implements RecipeType<CampfireCookingRecipe> {
        private Type() {

        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "hc_campfire_cooking";
    }

    public static class Serializer implements RecipeSerializer<CampfireCookingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "hc_campfire_cooking";
        // this is the name given in the json file

        @Override
        public CampfireCookingRecipe read(Identifier id, JsonObject json) {
            ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
            Ingredient input = Ingredient.fromJson(json.get("ingredient"));
            float experience = JsonHelper.getFloat(json, "experience");
            int cookingtime = JsonHelper.getInt(json, "cookingtime");
            int fuelticks = JsonHelper.getInt(json, "fuelticks");

            return new CampfireCookingRecipe(id, "campfire_cookable", input, result, experience, cookingtime);
        }

        @Override
        public CampfireCookingRecipe read(Identifier id, PacketByteBuf buf) {
            int numIngredients = buf.readInt();
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(numIngredients, Ingredient.EMPTY);
            for (int i = 0; i < numIngredients; i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }
            ItemStack output = buf.readItemStack();
            float experience = buf.readFloat();
            int cookingTime = buf.readInt();

            return new CampfireCookingRecipe(id, "cookable_foods", inputs.get(0), output, experience, cookingTime);
        }


        @Override
        public void write(PacketByteBuf buf, CampfireCookingRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }

            buf.writeItemStack(recipe.getOutput());
        }


    }
}