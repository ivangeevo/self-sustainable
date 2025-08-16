package net.ivangeevo.self_sustainable.recipe.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ivangeevo.self_sustainable.mixin.ShapedRecipeAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;

public class ShapedRecipeWithDamage extends ShapedRecipe {

    final int damage;

    public ShapedRecipeWithDamage(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification, int damage) {
        super(group, category, raw, result, showNotification);
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

    public static class Type implements RecipeType<ShapedRecipeWithDamage> {
        public static final ShapedRecipeWithDamage.Type INSTANCE = new ShapedRecipeWithDamage.Type();
        public static final String ID = "crafting_shaped_with_damage";
    }

    public static class Serializer implements RecipeSerializer<ShapedRecipeWithDamage> {

        public static final ShapedRecipeWithDamage.Serializer INSTANCE = new Serializer();
        public static final String ID = "crafting_shaped_with_damage";

        public static final MapCodec<ShapedRecipeWithDamage> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING
                                .optionalFieldOf("group", "")
                                .forGetter(ShapedRecipeWithDamage::getGroup),
                        CraftingRecipeCategory.CODEC
                                .optionalFieldOf("category", CraftingRecipeCategory.MISC)
                                .forGetter(ShapedRecipeWithDamage::getCategory),
                        RawShapedRecipe.CODEC
                                .forGetter(recipe -> ((ShapedRecipeAccessor) recipe).getRaw()),
                        ItemStack.VALIDATED_CODEC
                                .fieldOf("result")
                                .forGetter(recipe -> recipe.getResult(null)),
                        Codec.BOOL
                                .optionalFieldOf("show_notification", true)
                                .forGetter(ShapedRecipeWithDamage::showNotification),
                        Codec.INT
                                .fieldOf("damage")
                                .forGetter(ShapedRecipeWithDamage::getDamage)
                ).apply(instance, ShapedRecipeWithDamage::new)
        );

        public static final PacketCodec<RegistryByteBuf, ShapedRecipeWithDamage> PACKET_CODEC = PacketCodec.ofStatic(
                ShapedRecipeWithDamage.Serializer::write, ShapedRecipeWithDamage.Serializer::read
        );

        @Override
        public MapCodec<ShapedRecipeWithDamage> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, ShapedRecipeWithDamage> packetCodec() {
            return PACKET_CODEC;
        }

        private static ShapedRecipeWithDamage read(RegistryByteBuf buf) {
            String group = buf.readString();
            CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe raw = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);
            boolean showNotification = buf.readBoolean();
            int damage = buf.readVarInt();
            return new ShapedRecipeWithDamage(group, category, raw, result, showNotification, damage);
        }

        private static void write(RegistryByteBuf buf, ShapedRecipeWithDamage recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            RawShapedRecipe raw = ((ShapedRecipeAccessor) recipe).getRaw();
            RawShapedRecipe.PACKET_CODEC.encode(buf, raw);
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
            buf.writeBoolean(recipe.showNotification());
            buf.writeVarInt(recipe.getDamage());
        }
    }
}
