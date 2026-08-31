package org.btwr.self_sustainable.loot;

import net.minecraft.loot.condition.LootConditionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.SelfSustainableMod;

public class ModLootConditionTypes {
    public static final LootConditionType STRONG_CHEST_AXE = Registry.register(
            Registries.LOOT_CONDITION_TYPE,
            Identifier.of(SelfSustainableMod.MOD_ID, "strong_chest_axe"),
            new LootConditionType(StrongChestAxeLootCondition.CODEC)
    );

    public static void initialize() {}
}