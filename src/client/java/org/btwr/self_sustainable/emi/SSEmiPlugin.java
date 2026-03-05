package org.btwr.self_sustainable.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.recipe.EmiCookingRecipe;
import dev.emi.emi.recipe.special.EmiRepairItemRecipe;
import dev.emi.emi.runtime.EmiReloadLog;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.recipe.KnittingRecipe;
import org.btwr.self_sustainable.recipe.WickerWeavingRecipe;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.Item;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.shared_library.util.utils.IdUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;


public class SSEmiPlugin implements EmiPlugin {

    public static EmiRecipeCategory OVEN_COOKING = category("oven_cooking", EmiStack.of(ModBlocks.OVEN_BRICK));
    public static EmiRecipeCategory PROGRESSIVE_CRAFTING = category("progressive_crafting", EmiStack.of(ModItems.KNITTING));

    // Separate recipes for WICKER WEAVING & KNITTING, since they are custom recipes. This is so their crafting recipes can show in EMI
    public static EmiRecipeCategory KNITTING = category("knitting", EmiStack.of(Items.CRAFTING_TABLE));
    public static EmiRecipeCategory WICKER_WEAVING = category("wicker_weaving", EmiStack.of(Items.CRAFTING_TABLE));

    public static EmiRecipeCategory category(String id, EmiStack icon) {
        return new EmiRecipeCategory(Identifier.of(SelfSustainableMod.MOD_ID, id), icon, icon);
    }

    @Override
    public void register(EmiRegistry registry) {
        this.registerOvenCooking(registry);
        this.registerProgressiveCrafting(registry);
        this.registerBrickSunDrying(registry);

        this.registerRemovedRecipes(registry);
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
        registry.addRecipeHandler(ScreenHandlerType.FURNACE, new CookingRecipeHandler<>(PROGRESSIVE_CRAFTING));

        // Wicker weaving
        this.addWickerWeaving(registry);
        // Knitting
        this.addKnitting(registry);
    }

    private void addWickerWeaving(EmiRegistry registry) {
        registerFakeProgression(registry, EmiStack.of(ModItems.WICKER_WEAVING), EmiStack.of(ModItems.WICKER));

        for (WickerWeavingRecipe recipe : getRecipes(registry, WickerWeavingRecipe.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> {
                DefaultedList<EmiIngredient> grid = DefaultedList.ofSize(9, EmiIngredient.of(Ingredient.empty()));
                grid.set(0, EmiIngredient.of(Ingredient.ofItems(Items.SUGAR_CANE)));
                grid.set(1, EmiIngredient.of(Ingredient.ofItems(Items.SUGAR_CANE)));
                grid.set(3, EmiIngredient.of(Ingredient.ofItems(Items.SUGAR_CANE)));
                grid.set(4, EmiIngredient.of(Ingredient.ofItems(Items.SUGAR_CANE)));
                return new EmiCraftingRecipe(
                        grid,
                        EmiStack.of(recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager())),
                        EmiPort.getId(recipe),
                        false
                );
            }, recipe);
        }
    }

    private void addKnitting(EmiRegistry registry) {
        ModItems.WOOL_KNITS.forEach(((color, item) -> {
            Item woolKnitItem = Registries.ITEM.get(IdUtils.ofSS(color.getName() + "_wool_knit"));
            ItemStack knittingStack = new ItemStack(ModItems.KNITTING);
            knittingStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color.getEntityColor(), false));
            registerFakeProgression(registry, EmiStack.of(knittingStack), EmiStack.of(woolKnitItem));
        }));

        for (KnittingRecipe recipe : getRecipes(registry, KnittingRecipe.Type.INSTANCE)) {
            addRecipeSafe(registry, () -> {
                DefaultedList<EmiIngredient> grid = DefaultedList.ofSize(9, EmiIngredient.of(Ingredient.empty()));
                grid.set(3, EmiIngredient.of(Ingredient.ofItems(ModItems.KNITTING_NEEDLES)));
                grid.set(1, EmiIngredient.of(Ingredient.fromTag(ModTags.Items.KNITTING_INGREDIENTS)));
                grid.set(4, EmiIngredient.of(Ingredient.fromTag(ModTags.Items.KNITTING_INGREDIENTS)));
                return new EmiCraftingRecipe(
                        grid,
                        EmiStack.of(recipe.getResult(MinecraftClient.getInstance().world.getRegistryManager())),
                        EmiPort.getId(recipe),
                        false
                );
            }, recipe);
        }
    }

    private void registerBrickSunDrying(EmiRegistry registry) {
        registry.addRecipe(EmiWorldInteractionRecipe.builder()
                .id(Identifier.of("emi", "/world/block_interaction/self_sustainable/brick_sundrying"))
                .leftInput(EmiIngredient.of(Ingredient.ofItems(ModItems.BRICK_UNFIRED)))
                .rightInput(EmiIngredient.of(Ingredient.ofItems(Items.CLOCK)), false, sw -> {
                    sw.appendTooltip(Text.translatable("emi.brick_sundrying.tooltip"));
                    return sw;
                }).output(EmiStack.of(Items.BRICK)).supportsRecipeTree(true).build());
    }

    private void registerRemovedRecipes(EmiRegistry registry) {
        // Wicker Weaving
        registry.removeRecipes(recipe ->
                recipe.getCategory() == VanillaEmiRecipeCategories.ANVIL_REPAIRING &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.WICKER_WEAVING)
        );
        registry.removeRecipes(recipe ->
                recipe.getCategory() == VanillaEmiRecipeCategories.GRINDING &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.WICKER_WEAVING)
        );
        registry.removeRecipes(recipe ->
                recipe instanceof EmiRepairItemRecipe &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.WICKER_WEAVING)
        );

        // Knitting
        registry.removeRecipes(recipe ->
                recipe.getCategory() == VanillaEmiRecipeCategories.ANVIL_REPAIRING &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.KNITTING)
        );
        registry.removeRecipes(recipe ->
                recipe.getCategory() == VanillaEmiRecipeCategories.GRINDING &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.KNITTING)
        );
        registry.removeRecipes(recipe ->
                recipe instanceof EmiRepairItemRecipe &&
                        recipe.getOutputs().stream().anyMatch(stack -> stack.getItemStack().getItem() == ModItems.KNITTING)
        );
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

    private void registerFakeProgression(EmiRegistry registry, EmiStack input, EmiStack output) {
        EmiRecipe fakeRecipe = new EmiRecipe() {

            @Override
            public EmiRecipeCategory getCategory() {
                return PROGRESSIVE_CRAFTING;
            }

            @Override
            public @Nullable Identifier getId() {
                return Identifier.of(SelfSustainableMod.MOD_ID, "progression_" + Registries.ITEM.getId(output.getItemStack().getItem()).getPath());
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