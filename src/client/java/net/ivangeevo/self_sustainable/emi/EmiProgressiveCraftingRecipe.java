package net.ivangeevo.self_sustainable.emi;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.util.Identifier;

import java.util.List;

public class EmiProgressiveCraftingRecipe implements EmiRecipe {

    private final Identifier id;
    private final EmiRecipeCategory category;
    private final EmiIngredient input;
    private final EmiStack output;

    public EmiProgressiveCraftingRecipe(ShapedRecipeWithDamage recipe, EmiRecipeCategory category) {
        this.id = EmiPort.getId(recipe);
        this.category = category;
        input = EmiIngredient.of(recipe.getIngredients().getFirst());
        output = EmiStack.of(EmiPort.getOutput(recipe));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return category;
    }

    @Override
    public Identifier getId() {
        return id;
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
                                EmiPort.ordered(EmiPort.translatable("emi.wicker_weaving.time", 200 / 20f))
                                )
                        )
                );
        widgets.addSlot(input, 5, 2);
        widgets.addSlot(output, 55, 2);
    }

}