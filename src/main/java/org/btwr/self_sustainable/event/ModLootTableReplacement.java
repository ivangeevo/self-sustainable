package org.btwr.self_sustainable.event;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.block.Block;
import net.minecraft.data.server.loottable.BlockLootTableGenerator;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

public abstract class ModLootTableReplacement extends BlockLootTableGenerator {

    private static final List<Identifier> planks = List.of(
            Identifier.ofVanilla("oak_planks"),
            Identifier.ofVanilla("spruce_planks"),
            Identifier.ofVanilla("birch_planks"),
            Identifier.ofVanilla("jungle_planks"),
            Identifier.ofVanilla("acacia_planks"),
            Identifier.ofVanilla("dark_oak_planks")
            // Add more plank types as needed
    );

    protected ModLootTableReplacement(Set<Item> explosionImmuneItems, FeatureSet requiredFeatures, RegistryWrapper.WrapperLookup registryLookup) {
        super(explosionImmuneItems, requiredFeatures, registryLookup);
    }

    // Register loot table replacements
    public static void initialize() {
        //. replacePlanksWithToolCondition();
    }

    // Method to replace planks' loot tables with a tool-based condition using AlternativeEntry.builder
    public static void replacePlanksWithToolCondition() {
        LootTableEvents.REPLACE.register((key, original, source, registries) -> {
            if (source.isBuiltin()) {
                for (Identifier blockId : planks) {
                    Block blockToModify = Registries.BLOCK.get(blockId);

                    if (blockToModify.getLootTableKey().equals(key)) {
                        LootTable.Builder newTable = LootTable.builder();

                        // Add the alternative entry to the loot table
                        newTable.pool(LootPool.builder());

                        return newTable.build();
                    }
                }
            }
            return null;
        });
    }

}