package ivangeevo.selfsustainable.recipe.types.customcrafting;

import ivangeevo.selfsustainable.ModItems;
import ivangeevo.selfsustainable.recipe.serializer.KnittingRecipeSerializer;
import ivangeevo.selfsustainable.tag.ModTags;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class KnittingRecipe implements Recipe<Inventory> {

    // temporary variables used while processing recipes.
    private ItemStack stackNeedles;
    private ItemStack stackWool;
    private ItemStack stackWool2;

    private final Ingredient inputA;
    private final Ingredient inputB;
    private final ItemStack outputStack;
    private final Identifier id;


    public KnittingRecipe(Ingredient inputA, Ingredient inputB, ItemStack outputStack, Identifier id) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.outputStack = outputStack;
        this.id = id;
    }

    public Ingredient getInputA() {
        return inputA;
    }

    public Ingredient getInputB() {
        return inputB;
    }

    public static class Type implements RecipeType<KnittingRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "knitting_recipe";
    }


    @Override
    public boolean matches(Inventory inventory, World world) {
        return checkForIngredients(inventory);
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        if ( checkForIngredients(inventory) )
        {
            ItemStack resultStack = new ItemStack(ModItems.KNITTING, 1);

            int maxDamage = KnittingItem.DEFAULT_MAX_DAMAGE;
            resultStack.setDamage(maxDamage - 1);


            // messed up mixing in order to match color of final wool knit output that uses only 16 colors
            //int iWoolColor = WoolColorsHelper.woolColors[WoolItem.getClosestColorIndex(WoolItem.averageWoolColorsInGrid(inventory))];
            DyeColor iWoolColor = DyeColor.byId(WoolItem.getClosestColorIndex(WoolItem.averageWoolColorsInGrid(inventory)));

            KnittingItem.setColor(resultStack, iWoolColor);

            return resultStack;
        }

        return null;
    }

    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getOutput() {
        return null;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KnittingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    //------------- Class Specific Methods ------------//

    private boolean checkForIngredients(Inventory inventory)
    {
        stackNeedles = null;
        stackWool = null;
        stackWool2 = null;

        for ( int iTempSlot = 0; iTempSlot < inventory.size(); iTempSlot++ )
        {
            ItemStack tempStack = inventory.getStack( iTempSlot );

            if (tempStack != null)
            {
                if ( tempStack.getItem() == ModItems.KNITTING_NEEDLES)
                {
                    if (stackNeedles == null )
                    {
                        stackNeedles = tempStack;
                    }
                    else
                    {
                        return false;
                    }
                }
                else if (tempStack.isIn(ModTags.Items.WOOL_ITEMS))
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
