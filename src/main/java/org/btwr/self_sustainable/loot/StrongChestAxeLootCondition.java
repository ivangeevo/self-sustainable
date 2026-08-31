package org.btwr.self_sustainable.loot;

import com.mojang.serialization.MapCodec;
import net.minecraft.item.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.tag.ItemTags;
import org.btwr.self_sustainable.tag.ModTags;

import java.util.Set;

public class StrongChestAxeLootCondition implements LootCondition {
    public static final StrongChestAxeLootCondition INSTANCE = new StrongChestAxeLootCondition();

    public static final MapCodec<StrongChestAxeLootCondition> CODEC = MapCodec.unit(INSTANCE);

    private StrongChestAxeLootCondition() {}

    public static LootCondition.Builder builder() {
        return () -> INSTANCE;
    }

    @Override
    public LootConditionType getType() {
        return ModLootConditionTypes.STRONG_CHEST_AXE;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return Set.of(LootContextParameters.TOOL);
    }

    @Override
    public boolean test(LootContext lootContext) {
        ItemStack stack = lootContext.get(LootContextParameters.TOOL);

        if (stack == null || stack.isEmpty()) return false;

        Item item = stack.getItem();

        if (item instanceof ToolItem toolItem) {
            ToolMaterial toolMaterial = toolItem.getMaterial();

            boolean isAxe = stack.isIn(ItemTags.AXES);
            boolean aboveStoneSpeed = toolMaterial.getMiningSpeedMultiplier() > ToolMaterials.STONE.getMiningSpeedMultiplier();
            boolean isGoldToolMaterial = toolMaterial == ToolMaterials.GOLD;

            if (isAxe && aboveStoneSpeed && !isGoldToolMaterial) {
                return true;
            }
        }

        return stack.isIn(ModTags.Items.AXES_CAN_HARVEST_CHEST);
    }
}
