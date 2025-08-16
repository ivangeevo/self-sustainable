package net.ivangeevo.self_sustainable.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.handler.CraftingRecipeHandler;
import dev.emi.emi.recipe.EmiCookingRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.recipe.crafting.ShapedRecipeWithDamage;
import net.minecraft.block.Blocks;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;


public class SSEmiPlugin implements EmiPlugin
{
    public static EmiRecipeCategory CRAFTING_SHAPED_WITH_DAMAGE = category("crafting_shaped_with_damage", EmiStack.of(Blocks.CRAFTING_TABLE));
    public static EmiRecipeCategory OVEN_COOKING = category("oven_cooking", EmiStack.of(ModBlocks.OVEN_BRICK));
    public static EmiRecipeCategory PROGRESSIVE_CRAFTING = category("progressive_crafting", EmiStack.of(ModItems.WICKER_WEAVING));


    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Identifier.of(SelfSustainableMod.MOD_ID, id), icon, icon::render);
    }

    @Override
    public void register(EmiRegistry registry) {
        this.registerCraftingShapedWithDamage(registry);
        this.registerOvenCooking(registry);
        this.registerProgressiveCrafting(registry);
    }

    private void registerCraftingShapedWithDamage(EmiRegistry registry) {
        registry.addCategory(CRAFTING_SHAPED_WITH_DAMAGE);

        registry.addWorkstation(CRAFTING_SHAPED_WITH_DAMAGE, EmiStack.of(Blocks.CRAFTING_TABLE));
        registry.addRecipeHandler(ScreenHandlerType.CRAFTING, new CraftingRecipeHandler());

        for (ShapedRecipeWithDamage recipe : getRecipes(registry, ShapedRecipeWithDamage.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> new EmiShapedRecipeWithDamage(recipe), recipe);
        }
    }

    private void registerOvenCooking(EmiRegistry registry) {
        registry.addCategory(OVEN_COOKING);

        registry.addWorkstation(OVEN_COOKING, EmiStack.of(ModBlocks.OVEN_BRICK));
        registry.addRecipeHandler(ScreenHandlerType.FURNACE, new CookingRecipeHandler<>(OVEN_COOKING));

        for (OvenCookingRecipe recipe : getRecipes(registry, OvenCookingRecipe.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> new EmiCookingRecipe(recipe, OVEN_COOKING, 1, false), recipe);
        }
    }

    private void registerProgressiveCrafting(EmiRegistry registry) {
        registry.addCategory(PROGRESSIVE_CRAFTING);

        // TODO: Change workstation to have a knitting icon
        registry.addWorkstation(PROGRESSIVE_CRAFTING, EmiStack.of(ModItems.WICKER));
        registry.addRecipeHandler(ScreenHandlerType.FURNACE, new CookingRecipeHandler<>(OVEN_COOKING));

        for (ShapedRecipeWithDamage recipe : getRecipes(registry, ShapedRecipeWithDamage.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> new EmiProgressiveCraftingRecipe(recipe, PROGRESSIVE_CRAFTING), recipe);
        }
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing vanilla a" + SelfSustainableMod.MOD_ID + "recipe" + EmiPort.getId(recipe));
            EmiReloadLog.error(e);
        }
    }


    private static <C extends RecipeInput, T extends Recipe<C>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type).stream().map(RecipeEntry::value)::iterator;
    }
}
