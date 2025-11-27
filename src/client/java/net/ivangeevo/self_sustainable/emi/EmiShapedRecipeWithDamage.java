package net.ivangeevo.self_sustainable.emi;

import dev.emi.emi.recipe.EmiShapedRecipe;
import net.minecraft.recipe.ShapedRecipe;

public class EmiShapedRecipeWithDamage extends EmiShapedRecipe {

    protected int damage;

    public EmiShapedRecipeWithDamage(ShapedRecipe recipe) {
        super(recipe);
    }

    public int getDamage() {
        return damage;
    }

}