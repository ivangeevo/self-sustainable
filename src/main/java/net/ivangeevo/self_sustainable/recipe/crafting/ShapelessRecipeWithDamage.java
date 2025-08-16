package net.ivangeevo.self_sustainable.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class ShapelessRecipeWithDamage extends ShapelessRecipe {

    final int damage;

    public ShapelessRecipeWithDamage(String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients, int damage) {
        super(group, category, result, ingredients);
        this.damage = damage;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        ItemStack result = super.craft(input, lookup);
        result.setDamage(this.getDamage());
        return result;
    }

    public int getDamage() {
        return damage;
    }

    public static class Type implements RecipeType<ShapelessRecipeWithDamage> {
        public static final ShapelessRecipeWithDamage.Type INSTANCE = new ShapelessRecipeWithDamage.Type();
        public static final String ID = "crafting_shapeless_with_damage";
    }

    public static class Serializer implements RecipeSerializer<ShapelessRecipeWithDamage> {

        public static final ShapelessRecipeWithDamage.Serializer INSTANCE = new Serializer();
        public static final String ID = "crafting_shapeless_with_damage";

        private static final MapCodec<ShapelessRecipeWithDamage> CODEC = RecordCodecBuilder.mapCodec((instance) ->
                instance.group(

                        Codec.STRING
                                .optionalFieldOf("group", "")
                                .forGetter(ShapelessRecipeWithDamage::getGroup),
                        CraftingRecipeCategory.CODEC
                                .fieldOf("category").orElse(CraftingRecipeCategory.MISC)
                                .forGetter(ShapelessRecipeWithDamage::getCategory),
                        ItemStack.VALIDATED_CODEC
                                .fieldOf("result")
                                .forGetter((recipe) -> recipe.getResult(null)),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .listOf()
                                .fieldOf("ingredients")
                                .flatXmap(Serializer::validateIngredients, DataResult::success)
                                .forGetter(ShapelessRecipeWithDamage::getIngredients),
                        Codec.INT
                                .fieldOf("damage")
                                .forGetter(ShapelessRecipeWithDamage::getDamage)

                        ).apply(instance, ShapelessRecipeWithDamage::new)
        );

        private static DataResult<DefaultedList<Ingredient>> validateIngredients(List<Ingredient> ingredients) {
            Ingredient[] filtered = ingredients.stream()
                    .filter(i -> !i.isEmpty())
                    .toArray(Ingredient[]::new);

            if (filtered.length == 0) {
                return DataResult.error(() -> "No ingredients for shapeless recipe");
            } else if (filtered.length > 9) {
                return DataResult.error(() -> "Too many ingredients for shapeless recipe");
            } else {
                return DataResult.success(DefaultedList.copyOf(Ingredient.EMPTY, filtered));
            }
        }



        public static final PacketCodec<RegistryByteBuf, ShapelessRecipeWithDamage> PACKET_CODEC = PacketCodec.ofStatic(
                ShapelessRecipeWithDamage.Serializer::write, ShapelessRecipeWithDamage.Serializer::read
        );

        @Override
        public MapCodec<ShapelessRecipeWithDamage> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ShapelessRecipeWithDamage> packetCodec() {
            return PACKET_CODEC;
        }

        private static ShapelessRecipeWithDamage read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            int i = buf.readVarInt();
            DefaultedList<Ingredient> defaultedList = DefaultedList.ofSize(i, Ingredient.EMPTY);
            defaultedList.replaceAll((empty) -> Ingredient.PACKET_CODEC.decode(buf));
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            int damage = buf.readVarInt();
            return new ShapelessRecipeWithDamage(string, craftingRecipeCategory, itemStack, defaultedList, damage);
        }

        private static void write(RegistryByteBuf buf, ShapelessRecipeWithDamage recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            buf.writeVarInt(recipe.getIngredients().size());

            for(Ingredient ingredient : recipe.getIngredients()) {
                Ingredient.PACKET_CODEC.encode(buf, ingredient);
            }

            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
            buf.writeVarInt(recipe.getDamage());
        }
    }
}
