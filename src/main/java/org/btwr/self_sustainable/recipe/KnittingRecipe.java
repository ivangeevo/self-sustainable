package org.btwr.self_sustainable.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.items.KnittingItem;
import org.btwr.self_sustainable.registry.KnittingColorRegistry;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KnittingRecipe implements CraftingRecipe {

    private final String group;
    private final CraftingRecipeCategory category;

    public KnittingRecipe(String group, CraftingRecipeCategory category) {
        this.group = group;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        boolean hasNeedles = false;
        int woolCount = 0;

        for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
            ItemStack stack = craftingRecipeInput.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.getItem() == ModItems.KNITTING_NEEDLES) {
                hasNeedles = true;
            }
            else if (stack.isIn(ModTags.Items.KNITTING_INGREDIENTS)) {
                woolCount++;
            }
            else {
                return false;
            }
        }
        return hasNeedles && woolCount == 2;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack wool1 = ItemStack.EMPTY;
        ItemStack wool2 = ItemStack.EMPTY;

        for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
            ItemStack stack = craftingRecipeInput.getStackInSlot(i);
            if (stack.isEmpty() || stack.getItem() == ModItems.KNITTING_NEEDLES) continue;

            if (wool1.isEmpty()) {
                wool1 = stack;
            }
            else {
                wool2 = stack;
            }
        }

        if (wool1.isEmpty() || wool2.isEmpty()) return ItemStack.EMPTY;

        // Get colors from both wool items
        DyeColor color1 = KnittingColorRegistry.getColor(wool1);
        DyeColor color2 = KnittingColorRegistry.getColor(wool2);

        if (color1 == null || color2 == null) return ItemStack.EMPTY;


        // Create knitting item with blended or matched color
        DyeColor resultColor = color1 == color2 ? color1 : blendColors(color1, color2);

        ItemStack result = new ItemStack(ModItems.KNITTING);
        result.setDamage(KnittingItem.DEFAULT_MAX_DAMAGE - 1); // just above 0 so the durability bar appears
        KnittingItem.setColor(result, resultColor);
        return result;
    }

    private DyeColor blendColors(DyeColor c1, DyeColor c2) {
        int rgb1 = c1.getFireworkColor();
        int rgb2 = c2.getFireworkColor();
        int r = (((rgb1 >> 16) & 0xFF) + ((rgb2 >> 16) & 0xFF)) / 2;
        int g = (((rgb1 >> 8) & 0xFF) + ((rgb2 >> 8) & 0xFF)) / 2;
        int b = ((rgb1 & 0xFF) + (rgb2 & 0xFF)) / 2;
        int blended = (r << 16) | (g << 8) | b;

        return Arrays.stream(DyeColor.values())
                .min(Comparator.comparingInt(c -> colorDistance(c.getFireworkColor(), blended)))
                .orElse(DyeColor.WHITE);
    }

    private int colorDistance(int rgb1, int rgb2) {
        int dr = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);
        int dg = ((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF);
        int db = (rgb1 & 0xFF) - (rgb2 & 0xFF);
        return dr * dr + dg * dg + db * db;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 3;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return new ItemStack(ModItems.KNITTING);
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public CraftingRecipeCategory getCategory() {
        return category;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<KnittingRecipe> {
        public static final KnittingRecipe.Type INSTANCE = new KnittingRecipe.Type();
        public static final String ID = "knitting";
    }

    public static class Serializer implements RecipeSerializer<KnittingRecipe> {
        public static final KnittingRecipe.Serializer INSTANCE = new KnittingRecipe.Serializer();
        public static final String ID = "knitting";

        private static final MapCodec<KnittingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING
                                        .optionalFieldOf("group", "")
                                        .forGetter(KnittingRecipe::getGroup),
                                CraftingRecipeCategory.CODEC
                                        .fieldOf("category")
                                        .orElse(CraftingRecipeCategory.MISC)
                                        .forGetter(KnittingRecipe::getCategory)
                        )
                        .apply(instance, KnittingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, KnittingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                KnittingRecipe.Serializer::write, KnittingRecipe.Serializer::read
        );

        @Override
        public MapCodec<KnittingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, KnittingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static KnittingRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            return new KnittingRecipe(string, craftingRecipeCategory);
        }

        private static void write(RegistryByteBuf buf, KnittingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        private final RecipeCategory category;
        private final Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap<>();
        @Nullable
        private String group;

        public JsonBuilder(RecipeCategory category) {
            this.category = category;
        }

        public static JsonBuilder create() {
            return new JsonBuilder(RecipeCategory.MISC);
        }

        public static JsonBuilder create(RecipeCategory category) {
            return new JsonBuilder(category);
        }

        public JsonBuilder criterion(String name, AdvancementCriterion<?> criterion) {
            this.advancementBuilder.put(name, criterion);
            return this;
        }

        public JsonBuilder group(@Nullable String group) {
            this.group = group;
            return this;
        }

        @Override
        public Item getOutputItem() {
            return ModItems.KNITTING;
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder()
                    .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                    .rewards(AdvancementRewards.Builder.recipe(recipeId))
                    .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.advancementBuilder.forEach(builder::criterion);

            KnittingRecipe recipe = new KnittingRecipe(
                    Objects.requireNonNullElse(this.group, ""),
                    CraftingRecipeJsonBuilder.toCraftingCategory(this.category)
            );

            exporter.accept(recipeId, recipe, builder.build(
                    recipeId.withPrefixedPath("recipes/" + this.category.getName() + "/")
            ));
        }

        private void validate(Identifier recipeId) {
            if (this.advancementBuilder.isEmpty()) {
                throw new IllegalStateException("No way of obtaining recipe " + recipeId);
            }
        }
    }
}
