package net.ivangeevo.self_sustainable.block.utils;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.entity.TorchBE;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.SetDamageLootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class FuelBasedLootModifier {
    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (isFuelHavingTorch(key)) { // Modify the loot table of torches
                tableBuilder.modifyPools(pool -> {
                    pool.apply(SetDamageLootFunction.builder(getFuelBasedDamage()).build());
                });
            }


        });
    }

    private static boolean isFuelHavingTorch(RegistryKey<LootTable> key) {
        return key.getRegistry().equals(Identifier.of(SelfSustainableMod.MOD_ID, "blocks/crude_torch_lit"))
                || key.getRegistry().equals(Identifier.of(SelfSustainableMod.MOD_ID, "blocks/crude_torch_smoulder"))
                || key.getRegistry().equals(Identifier.of(SelfSustainableMod.MOD_ID, "blocks/wall_torch_lit"))
                || key.getRegistry().equals(Identifier.of(SelfSustainableMod.MOD_ID, "blocks/wall_torch_smoulder"));

    }

    private static LootNumberProvider getFuelBasedDamage() {
        return new LootNumberProvider() {
            @Override
            public float nextFloat(LootContext context) {
                var blockEntity = context.get(LootContextParameters.BLOCK_ENTITY);

                if (blockEntity instanceof TorchBE torchEntity) {
                    return torchEntity.getFuel() / 100.0f; // Convert fuel (0-100) to a percentage
                }

                return 0.0f; // Default to undamaged
            }

            @Override
            public LootNumberProviderType getType() {
                return LootNumberProviderTypes.CONSTANT;
            }

        };
    }
}
