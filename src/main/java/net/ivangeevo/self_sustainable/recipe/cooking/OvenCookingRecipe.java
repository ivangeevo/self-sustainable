/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.recipe.cooking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;

public class OvenCookingRecipe extends AbstractCookingRecipe {
    public OvenCookingRecipe(String group, CookingRecipeCategory category, Ingredient input,
                             ItemStack output, float experience, int cookTime) {
        super(Type.INSTANCE, group, category, input, output, experience, cookTime);
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

    public static class Type implements RecipeType<OvenCookingRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "oven_cooking";
    }

    public static class Serializer implements RecipeSerializer<OvenCookingRecipe> {

        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "oven_cooking";

        private static final MapCodec<OvenCookingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING
                                        .optionalFieldOf("group", "")
                                        .forGetter(AbstractCookingRecipe::getGroup),
                                CookingRecipeCategory.CODEC
                                        .fieldOf("category")
                                        .orElse(CookingRecipeCategory.MISC)
                                        .forGetter(AbstractCookingRecipe::getCategory),
                                Ingredient.DISALLOW_EMPTY_CODEC
                                        .fieldOf("ingredient")
                                        .forGetter(recipe -> recipe.getIngredients().get(0)),
                                ItemStack.VALIDATED_CODEC
                                        .fieldOf("result")
                                        .forGetter(recipe -> recipe.getResult(null)),
                                Codec.FLOAT
                                        .fieldOf("experience")
                                        .forGetter(AbstractCookingRecipe::getExperience),
                                Codec.INT
                                        .fieldOf("cookingTime")
                                        .forGetter(AbstractCookingRecipe::getCookingTime)

                        )
                        .apply(instance, OvenCookingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, OvenCookingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                OvenCookingRecipe.Serializer::write, OvenCookingRecipe.Serializer::read
        );

        @Override
        public MapCodec<OvenCookingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, OvenCookingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static OvenCookingRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CookingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CookingRecipeCategory.class);
            Ingredient input = Ingredient.PACKET_CODEC.decode(buf);
            ItemStack output = ItemStack.PACKET_CODEC.decode(buf);
            float f = buf.readFloat();
            int i = buf.readVarInt();
            return new OvenCookingRecipe(string, craftingRecipeCategory, input, output, f, i);
        }

        public static void write(RegistryByteBuf buf, OvenCookingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
            Ingredient.PACKET_CODEC.encode(buf, recipe.getIngredients().getFirst());
            ItemStack.PACKET_CODEC.encode(buf, recipe.result);
            buf.writeFloat(recipe.experience);
            buf.writeVarInt(recipe.cookingTime);
        }

    }

    public OvenCookingRecipe create(String group, CookingRecipeCategory category, Ingredient ingredient,
                                    ItemStack result, float experience, int cookingTime) {
        return new OvenCookingRecipe(group, category, ingredient, result, experience, cookingTime);
    }

}

