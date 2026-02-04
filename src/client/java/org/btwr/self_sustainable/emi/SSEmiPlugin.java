package org.btwr.self_sustainable.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.recipe.EmiCookingRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class SSEmiPlugin implements EmiPlugin {

    public static EmiRecipeCategory CRAFTING_SHAPED_WITH_DAMAGE = category("crafting_shaped_with_damage", EmiStack.of(Blocks.CRAFTING_TABLE));
    public static EmiRecipeCategory OVEN_COOKING = category("oven_cooking", EmiStack.of(ModBlocks.OVEN_BRICK));
    public static EmiRecipeCategory PROGRESSIVE_CRAFTING = category("progressive_crafting", EmiStack.of(ModItems.WICKER_WEAVING));

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Identifier.of(SelfSustainableMod.MOD_ID, id), icon, icon::render);
    }

    @Override
    public void register(EmiRegistry registry) {
        //this.registerCraftingShapedWithDamage(registry);
        this.registerOvenCooking(registry);
        this.registerProgressiveCrafting(registry);
        this.registerBrickSunDrying(registry);
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
        registry.addRecipeHandler(ScreenHandlerType.FURNACE, new CookingRecipeHandler<>(PROGRESSIVE_CRAFTING));

        registerFakeProgression(registry, ModItems.WICKER_WEAVING, ModItems.WICKER);
        /**
        for (ShapedRecipeWithDamage recipe : getRecipes(registry, ShapedRecipeWithDamage.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> new EmiProgressiveCraftingRecipe(recipe, PROGRESSIVE_CRAFTING), recipe);
        }
         **/
    }

    private void registerBrickSunDrying(EmiRegistry registry) {
        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .id(Identifier.of("emi", "/world/block_interaction/self_sustainable/brick_sundrying"))
                .leftInput(EmiIngredient.of(Ingredient.ofItems(ModItems.BRICK_UNFIRED)))
                .rightInput(EmiIngredient.of(Ingredient.ofItems(Items.CLOCK)), false, sw -> {
                    sw.appendTooltip(Text.translatable("emi.tooltip.self_sustainable.brick_sundrying"));
                    return sw;
                }).output(EmiStack.of(Items.BRICK)).supportsRecipeTree(true).build());
    }

    private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, Recipe<?> recipe) {
        try {
            registry.addRecipe(supplier.get());
        }
        catch (Throwable e) {
            EmiReloadLog.warn("Exception thrown when parsing vanilla a" + SelfSustainableMod.MOD_ID + "recipe" + EmiPort.getId(recipe));
            EmiReloadLog.error(e);
        }
    }

    private void registerFakeProgression(EmiRegistry registry, Item inputItem, Item outputItem) {
        EmiStack input = EmiStack.of(inputItem);
        EmiStack output = EmiStack.of(outputItem);
        EmiRecipe fakeRecipe = new EmiRecipe() {

            @Override
            public EmiRecipeCategory getCategory() {
                return PROGRESSIVE_CRAFTING;
            }

            @Override
            public @Nullable Identifier getId() {
                return Identifier.of(SelfSustainableMod.MOD_ID, "progression_" + Registries.ITEM.getId(inputItem).getPath());
            }

            @Override
            public List<EmiIngredient> getInputs() {
                return List.of(input);
            }

            @Override
            public List<EmiStack> getOutputs() {
                return List.of(output);
            }

            @Override
            public int getDisplayWidth() {
                return 78;
            }

            @Override
            public int getDisplayHeight() {
                return 22;
            }

            @Override
            public void addWidgets(WidgetHolder widgets) {
                widgets.addFillingArrow(27, 2, 3000 * 20)
                        .tooltip((mx, my) -> List.of(TooltipComponent.of(
                                                EmiPort.ordered(EmiPort.translatable("emi.progressive_crafting.tooltip", 200 / 20f))
                                        )
                                )
                        );
                widgets.addSlot(input, 5, 2);
                widgets.addSlot(output, 55, 2).recipeContext(this);
            }
        };

        registry.addRecipe(fakeRecipe);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> Iterable<T> getRecipes(EmiRegistry registry, RecipeType<T> type) {
        return registry.getRecipeManager().listAllOfType(type).stream().map(RecipeEntry::value)::iterator;
    }

}