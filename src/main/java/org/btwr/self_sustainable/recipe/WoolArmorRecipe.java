package org.btwr.self_sustainable.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.mixin.ShapedRecipeAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WoolArmorRecipe extends ShapedRecipe {

    public WoolArmorRecipe(String group, CraftingRecipeCategory category, RawShapedRecipe raw, ItemStack result, boolean showNotification) {
        super(group, category, raw, result, showNotification);
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        int r = 0, g = 0, b = 0, count = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            // getOrDefault falls back to the item's default component if not overridden on the stack
            DyedColorComponent dyed = stack.getOrDefault(DataComponentTypes.DYED_COLOR, null);
            if (dyed == null) continue;

            int c = dyed.rgb();
            r += (c >> 16) & 0xFF;
            g += (c >> 8)  & 0xFF;
            b +=  c        & 0xFF;
            count++;
        }

        ItemStack result = getResult(lookup).copy();

        if (count > 0) {
            int rgb = ((r / count) << 16) | ((g / count) << 8) | (b / count);
            result.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(rgb, false));
        }

        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<WoolArmorRecipe> {
        public static final WoolArmorRecipe.Serializer INSTANCE = new WoolArmorRecipe.Serializer();
        public static final String ID = "wool_armor";

        public static final MapCodec<WoolArmorRecipe> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(WoolArmorRecipe::getGroup),
                        CraftingRecipeCategory.CODEC.fieldOf("category").forGetter(WoolArmorRecipe::getCategory),
                        RawShapedRecipe.CODEC.forGetter(r -> ((ShapedRecipeAccessor) r).getRaw()),
                        ItemStack.VALIDATED_CODEC.fieldOf("output").forGetter(r -> r.getResult(null)),
                        Codec.BOOL.optionalFieldOf("show_notification", true).forGetter(ShapedRecipe::showNotification)
                ).apply(instance, WoolArmorRecipe::new));

        public static final PacketCodec<RegistryByteBuf, WoolArmorRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                WoolArmorRecipe.Serializer::write, WoolArmorRecipe.Serializer::read
        );

        @Override
        public MapCodec<WoolArmorRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, WoolArmorRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static WoolArmorRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            RawShapedRecipe rawShapedRecipe = RawShapedRecipe.PACKET_CODEC.decode(buf);
            ItemStack itemStack = ItemStack.PACKET_CODEC.decode(buf);
            boolean bl = buf.readBoolean();
            return new WoolArmorRecipe(string, craftingRecipeCategory, rawShapedRecipe, itemStack, bl);
        }

        private static void write(RegistryByteBuf buf, WoolArmorRecipe recipe) {
            buf.writeString(recipe.getGroup());
            buf.writeEnumConstant(recipe.getCategory());
            RawShapedRecipe.PACKET_CODEC.encode(buf, ((ShapedRecipeAccessor) recipe).getRaw());
            ItemStack.PACKET_CODEC.encode(buf, recipe.getResult(null));
            buf.writeBoolean(((ShapedRecipeAccessor) recipe).getShowNotification());
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        private final RecipeCategory category;
        private final ItemStack output;
        private final int count;
        private final List<String> pattern = new ArrayList<>();
        private final Map<Character, Ingredient> inputs = new LinkedHashMap<>();
        private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();
        @Nullable
        private String group;
        private boolean showNotification = true;

        public JsonBuilder(RecipeCategory category, ItemStack output, int count) {
               this.category = category;
               this.output = output;
               this.count = count;
        }

        public static JsonBuilder create(RecipeCategory category, ItemStack output) {
            return create(category, output, 1);
        }

        public static JsonBuilder create(RecipeCategory category, ItemStack output, int count) {
            return new JsonBuilder(category, output, count);
        }

        public JsonBuilder pattern(String row) {
            pattern.add(row);
            return this;
        }

        public JsonBuilder input(Character c, Ingredient ingredient) {
            inputs.put(c, ingredient);
            return this;
        }

        public JsonBuilder input(Character c, ItemConvertible item) {
            return input(c, Ingredient.ofItems(item));
        }

        @Override
        public JsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
            criteria.put(name, criterion);
            return this;
        }

        @Override
        public JsonBuilder group(@Nullable String group) {
            this.group = group;
            return this;
        }

        public JsonBuilder showNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return this.output.getItem();
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            if (criteria.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }

            Advancement.Builder advancement = exporter.getAdvancementBuilder()
                    .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                    .rewards(AdvancementRewards.Builder.recipe(recipeId))
                    .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            criteria.forEach(advancement::criterion);

            RawShapedRecipe raw = RawShapedRecipe.create(inputs, pattern);
            WoolArmorRecipe recipe = new WoolArmorRecipe(
                    Objects.requireNonNullElse(group, ""),
                    CraftingRecipeJsonBuilder.toCraftingCategory(category),
                    raw,
                    output,
                    this.showNotification
            );

            exporter.accept(recipeId, recipe, advancement.build(
                    recipeId.withPrefixedPath("recipes/" + category.getName() + "/")
            ));
        }
    }

}