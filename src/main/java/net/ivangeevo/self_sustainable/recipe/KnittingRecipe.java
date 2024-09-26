// FCMOD

package net.ivangeevo.self_sustainable.recipe;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.item.items.KnittingItem;
import net.ivangeevo.self_sustainable.item.items.WoolItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
/**
public class KnittingRecipe implements Recipe<CraftingInventory> {




    // temporary variables used while processing recipes.
    private ItemStack stackNeedles;
    private ItemStack stackWool;
    private ItemStack stackWool2;

    public KnittingRecipe(Identifier id, DefaultedList<Ingredient> input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    // Recipe added methods

    private final Identifier id;
    private final ItemStack output;
    private final DefaultedList<Ingredient> input;


    @Override
    public boolean matches(CraftingInventory inventory, World world) {
        return checkForIngredients(inventory);
    }

    @Override
    public ItemStack craft(CraftingInventory input, RegistryWrapper.WrapperLookup lookup) {
        return null;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory, DynamicRegistryManager registryManager) {
        if ( checkForIngredients(inventory) )
        {
            ItemStack resultStack = new ItemStack( ModItems.KNITTING, 1);
            resultStack.setDamage(KnittingItem.DEFAULT_MAX_DAMAGE - 1);

            // messed up mixing in order to match color of final wool knit output that uses only 16 colors
            int iWoolColor = WoolItem.woolColors[WoolItem.getClosestColorIndex(WoolItem.averageWoolColorsInGrid(inventory))];

            KnittingItem.setColor(resultStack, DyeColor.byId(iWoolColor));

            return resultStack;
        }

        return null;
    }


    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        return null;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return output.copy();
    }



    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<KnittingRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "knitting";

    }

    // TODO: make the CODEC for Knitting recipes and refactor them as necessary.
    //  They weren't working before anyway.
    public static class Serializer implements RecipeSerializer<KnittingRecipe>
    {
        private static final MapCodec<KnittingRecipe> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                        Codec.STRING
                                .optionalFieldOf("group", "")
                                .forGetter(recipe -> recipe.group),
                        CookingRecipeCategory.CODEC
                                .fieldOf("category")
                                .orElse(CookingRecipeCategory.MISC)
                                .forGetter(recipe -> recipe.category),
                        Ingredient.DISALLOW_EMPTY_CODEC
                                .fieldOf("ingredient")
                                .forGetter(recipe -> recipe.ingredient),
                        ItemStack.VALIDATED_CODEC
                                .fieldOf("result")
                                .forGetter(recipe -> recipe.result),
                        Codec.FLOAT
                                .fieldOf("experience")
                                .forGetter(recipe -> recipe.experience),
                        Codec.INT
                                .fieldOf("cookingTime")
                                .forGetter(recipe -> recipe.cookingTime)

                ).apply(instance, KnittingRecipe::new)
        );

        public static final PacketCodec<RegistryByteBuf, KnittingRecipe> PACKET_CODEC = PacketCodec.ofStatic(
                KnittingRecipe.Serializer::write, KnittingRecipe.Serializer::read
        );

        @Override
        public MapCodec<KnittingRecipe> codec() {
            return CODEC;
        }
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "knitting";

        private static KnittingRecipe read(RegistryByteBuf buf)
        {
            Identifier id = buf.readIdentifier();
            Ingredient ingredient = Ingredient.PACKET_CODEC.decode(buf);
            DefaultedList<Ingredient> ingredientList = ingredient
            ItemStack result = ItemStack.PACKET_CODEC.decode(buf);

            return new KnittingRecipe()
        }

        private static void write(RegistryByteBuf buf, KnittingRecipe recipe)
        {

        }

        @Override
        public KnittingRecipe read(Identifier id, JsonObject json) {
            ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "output"));

            JsonArray ingredients = JsonHelper.getArray(json, "ingredients");
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(3, Ingredient.EMPTY);
            return null;
        }

        @Override
        public KnittingRecipe read(Identifier id, PacketByteBuf buf) {
            DefaultedList<Ingredient> inputs = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromPacket(buf));
            }

            ItemStack output = buf.readItemStack();
            return new KnittingRecipe(id, output, inputs);
        }

        @Override
        public void write(PacketByteBuf buf, KnittingRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buf);
            }
            buf.writeItemStack(recipe.getOutput(null));
        }

    }





    //------------- Class Specific Methods ------------//

    private boolean checkForIngredients(CraftingInventory inventory)
    {
        stackNeedles = null;
        stackWool = null;
        stackWool2 = null;

        for ( int iTempSlot = 0; iTempSlot < inventory.size(); iTempSlot++ )
        {
            ItemStack tempStack = inventory.getStack(iTempSlot);

            if (tempStack != null)
            {
                if ( tempStack.getItem() == ModItems.KNITTING_NEEDLES)
                {
                    if (stackNeedles == null)
                    {
                        stackNeedles = tempStack;
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (tempStack.getItem() == ModItems.WOOL)
                {
                    if (stackWool == null )
                    {
                        stackWool = tempStack;
                    }
                    else if (stackWool2 == null )
                    {
                        stackWool2 = tempStack;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }

        return stackNeedles != null && stackWool != null && stackWool2 != null;
    }





}

 **/
