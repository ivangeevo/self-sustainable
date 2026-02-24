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
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.items.WickerWeavingItem;
import org.btwr.self_sustainable.tag.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class WickerWeavingRecipe implements CraftingRecipe {

    private final String group;
    private final CraftingRecipeCategory category;

    public WickerWeavingRecipe(String group, CraftingRecipeCategory category) {
        this.group = group;
        this.category = category;
    }

    @Override
    public boolean matches(CraftingRecipeInput craftingRecipeInput, World world) {
        int wickerIngredientCount = 0;

        for (int i = 0; i < craftingRecipeInput.getSize(); i++) {
            ItemStack stack = craftingRecipeInput.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            if (stack.isOf(Items.SUGAR_CANE)) {
                wickerIngredientCount++;
            } else {
                // Something in the grid that isn't a valid ingredient
                return false;
            }
        }

        return wickerIngredientCount == 4;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput craftingRecipeInput, RegistryWrapper.WrapperLookup wrapperLookup) {
        ItemStack result = new ItemStack(ModItems.WICKER_WEAVING);
        result.setDamage(WickerWeavingItem.DEFAULT_MAX_DAMAGE - 1);
        return result;
    }

    @Override
    public boolean fits(int width, int height) {
        return width >= 2 && height >= 2;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return new ItemStack(ModItems.WICKER_WEAVING);
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

    public static class Type implements RecipeType<WickerWeavingRecipe> {
        public static final WickerWeavingRecipe.Type INSTANCE = new WickerWeavingRecipe.Type();
        public static final String ID = "wicker_weaving";
    }

    public static class Serializer implements RecipeSerializer<WickerWeavingRecipe> {
        public static final WickerWeavingRecipe.Serializer INSTANCE = new WickerWeavingRecipe.Serializer();
        public static final String ID = "wicker_weaving";

        private static final MapCodec<WickerWeavingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                Codec.STRING
                                        .optionalFieldOf("group", "")
                                        .forGetter(WickerWeavingRecipe::getGroup),
                                CraftingRecipeCategory.CODEC
                                        .fieldOf("category")
                                        .orElse(CraftingRecipeCategory.MISC)
                                        .forGetter(WickerWeavingRecipe::getCategory)
                        )
                        .apply(instance, WickerWeavingRecipe::new)
        );
        public static final PacketCodec<RegistryByteBuf, WickerWeavingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                WickerWeavingRecipe.Serializer::write, WickerWeavingRecipe.Serializer::read
        );

        @Override
        public MapCodec<WickerWeavingRecipe> codec() {
            return CODEC;
        }

        @Override
        public PacketCodec<RegistryByteBuf, WickerWeavingRecipe> packetCodec() {
            return PACKET_CODEC;
        }

        private static WickerWeavingRecipe read(RegistryByteBuf buf) {
            String string = buf.readString();
            CraftingRecipeCategory craftingRecipeCategory = buf.readEnumConstant(CraftingRecipeCategory.class);
            return new WickerWeavingRecipe(string, craftingRecipeCategory);
        }

        private static void write(RegistryByteBuf buf, WickerWeavingRecipe recipe) {
            buf.writeString(recipe.group);
            buf.writeEnumConstant(recipe.category);
        }
    }

    public static class JsonBuilder implements CraftingRecipeJsonBuilder {
        protected RecipeCategory category;
        protected Map<String, AdvancementCriterion<?>> advancementBuilder = new LinkedHashMap<>();

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
            return ModItems.WICKER_WEAVING;
        }

        @Override
        public void offerTo(RecipeExporter exporter, Identifier recipeId) {
            this.validate(recipeId);
            Advancement.Builder builder = exporter.getAdvancementBuilder()
                    .criterion("has_the_recipe", RecipeUnlockedCriterion.create(recipeId))
                    .rewards(AdvancementRewards.Builder.recipe(recipeId))
                    .criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
            this.advancementBuilder.forEach(builder::criterion);

            WickerWeavingRecipe recipe = new WickerWeavingRecipe(
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
