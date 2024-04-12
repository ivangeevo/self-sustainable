/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class OvenCookingRecipe extends AbstractCookingRecipe {
    public OvenCookingRecipe(Identifier id, String group, CookingRecipeCategory category, Ingredient input, ItemStack output, float experience, int cookTime) {
        super(Type.INSTANCE, id, group, category, input, output, experience, cookTime);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.OVEN_BRICK);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }
    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<OvenCookingRecipe>
    {
        public static final Type INSTANCE = new Type();
        public static final String ID = "oven_cooking";
    }
    public static class Serializer implements RecipeSerializer<OvenCookingRecipe>
    {
        private final int cookingTime;
        private final RecipeFactory<?> recipeFactory;
        public static final Serializer INSTANCE = new Serializer(OvenCookingRecipe::new, 200);
        public static final String ID = "oven_cooking";

        public Serializer(RecipeFactory<?> recipeFactory, int cookingTime)
        {
            this.cookingTime = cookingTime;
            this.recipeFactory = recipeFactory;
        }

        @Override
        public OvenCookingRecipe read(Identifier id, JsonObject json)
        {
            String string = JsonHelper.getString(json, "group", "");
            CookingRecipeCategory cookingRecipeCategory = CookingRecipeCategory.CODEC.byId(JsonHelper.getString(json, "category", null), CookingRecipeCategory.MISC);
            JsonElement jsonElement = JsonHelper.hasArray(json, "ingredient") ? JsonHelper.getArray(json, "ingredient") : JsonHelper.getObject(json, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonElement, false);
            String string2 = JsonHelper.getString(json, "result");
            Identifier identifier2 = new Identifier(string2);
            ItemStack itemStack = new ItemStack(Registries.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
            float f = JsonHelper.getFloat(json, "experience", 0.0F);
            int i = JsonHelper.getInt(json, "cookingtime", this.cookingTime);
            return (OvenCookingRecipe) this.recipeFactory.create(id, string, cookingRecipeCategory, ingredient, itemStack, f, i);
        }

        @Override
        public OvenCookingRecipe read(Identifier id, PacketByteBuf buf)
        {
            String string = buf.readString();
            CookingRecipeCategory cookingRecipeCategory = buf.readEnumConstant(CookingRecipeCategory.class);
            Ingredient ingredient = Ingredient.fromPacket(buf);
            ItemStack itemStack = buf.readItemStack();
            float f = buf.readFloat();
            int i = buf.readVarInt();
            return (OvenCookingRecipe) this.recipeFactory.create(id, string, cookingRecipeCategory, ingredient, itemStack, f, i);
        }

        @Override
        public void write(PacketByteBuf buf, OvenCookingRecipe recipe)
        {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.getCategory());
            recipe.input.write(buf);
            buf.writeItemStack(recipe.output);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookTime);
        }

        public interface RecipeFactory<T extends AbstractCookingRecipe> {
            T create(Identifier var1, String var2, CookingRecipeCategory var3, Ingredient var4, ItemStack var5, float var6, int var7);
        }
    }

}

