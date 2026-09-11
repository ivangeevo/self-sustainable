package org.btwr.self_sustainable.event.events;

import com.google.common.collect.ImmutableList;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.InvertedLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.loot.StrongChestAxeLootCondition;
import org.btwr.shared_library.mixin.accessors.ItemEntryAccessor;
import org.btwr.shared_library.mixin.accessors.LootPoolBuilderAccessor;
import org.btwr.shared_library.util.utils.IdUtils;

import java.util.ArrayList;
import java.util.List;

public class ModLootTableEvents {

    private static final String BASE_SHEEP_LOOT_TABLE = "entities/sheep/";

    public static void register() {
        LootTableEvents.MODIFY.register(ModLootTableEvents::modifySheepLoot);
        LootTableEvents.REPLACE.register(ModLootTableEvents::replaceChestLootTable);
    }

    // Replaces sheep wool block drops with the mod wool items
    private static void modifySheepLoot(RegistryKey<LootTable> key, LootTable.Builder tableBuilder, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        for (DyeColor color : DyeColor.values()) {
            Identifier sheepLootTableId = IdUtils.ofMC(BASE_SHEEP_LOOT_TABLE + color.getName());
            RegistryKey<LootTable> SHEEP_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, sheepLootTableId);

            String baseId = color.getName() + "_wool";
            Item woolItem = Registries.ITEM.get(IdUtils.ofSS(baseId));
            Item woolBlock = Registries.ITEM.get(IdUtils.ofMC(baseId));

            if (SHEEP_LOOT_TABLE.equals(key)) {
                replaceItemsInPools(tableBuilder, woolBlock, woolItem);
            }
        }
    }

    private static void replaceItemsInPools(LootTable.Builder tableBuilder, Item target, Item replacement) {
        tableBuilder.modifyPools(poolBuilder -> {
            List<LootPoolEntry> entries = new ArrayList<>(((LootPoolBuilderAccessor) poolBuilder).getEntries().build());
            entries.replaceAll(entry -> {
                if (!(entry instanceof ItemEntry itemEntry)) return entry;
                if (((ItemEntryAccessor) itemEntry).getItem().value() != target) return entry;
                ((ItemEntryAccessor) entry).setItem(Registries.ITEM.getEntry(replacement));
                return entry;
            });
            ((LootPoolBuilderAccessor) poolBuilder).setEntries(ImmutableList.<LootPoolEntry>builder().addAll(entries));
        });
    }

    private static LootTable replaceChestLootTable(RegistryKey<LootTable> key, LootTable original, LootTableSource source, RegistryWrapper.WrapperLookup registries) {
        //if (!source.isBuiltin()) {
        if (Blocks.CHEST.getLootTableKey().equals(key)) {
            LootTable.Builder newTable = LootTable.builder();

            newTable.pool(LootPool.builder()
                    .with(ItemEntry.builder(Items.CHEST))
                    .conditionally(withStrongAxe())
            );

            // Add a saw dust drop if Better With Time is present
            if (FabricLoader.getInstance().isModLoaded("bwt")) {
                 newTable.pool(LootPool.builder()
                 .with(ItemEntry.builder(Registries.ITEM.get(Identifier.of("bwt", "saw_dust")))
                 .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(6))))
                 .conditionally(withoutStrongAxe())
                 );
            }

            newTable.pool(LootPool.builder()
                    .with(ItemEntry.builder(Items.STICK)
                            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2))))
                    .conditionally(withoutStrongAxe())
            );

            return newTable.build();
        }
        //}
        return null;
    }

    private static LootCondition.Builder withStrongAxe() {
        return StrongChestAxeLootCondition.builder();
    }

    private static LootCondition.Builder withoutStrongAxe() {
        return InvertedLootCondition.builder(withStrongAxe());
    }

}
