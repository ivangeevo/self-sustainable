package net.ivangeevo.self_sustainable.client.emi;

import btwr.btwr_sl.lib.util.utils.RecipeProviderUtils;
import com.bwt.utils.Id;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.recipe.EmiCookingRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;


public class SSEmiPlugin implements EmiPlugin
{

    public static EmiRecipeCategory OVEN_COOKING = category("oven_cooking", EmiStack.of(ModBlocks.OVEN_BRICK));


    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Identifier.of(SelfSustainableMod.MOD_ID, id), icon, icon::render);
    }

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(OVEN_COOKING);

        registry.addWorkstation(OVEN_COOKING, EmiStack.of(ModBlocks.OVEN_BRICK));
        registry.addRecipeHandler(ScreenHandlerType.FURNACE, new CookingRecipeHandler<>(OVEN_COOKING));

        for (OvenCookingRecipe recipe : getRecipes(registry, OvenCookingRecipe.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> new EmiCookingRecipe(recipe, OVEN_COOKING, 1, false), recipe);
        }
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        } catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing vanilla recipe " + EmiPort.getId(recipe));
            EmiReloadLog.error(e);
        }
    }


    private static <C extends RecipeInput, T extends Recipe<C>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type).stream().map(RecipeEntry::value)::iterator;
    }
}
