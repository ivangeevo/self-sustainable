package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.item.component.ModComponentsTypes;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableLootTableProvider extends FabricBlockLootTableProvider {

    public SelfSustainableLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        this.addDrop(ModBlocks.BRICK, drops(Items.BRICK));
        this.addDrop(ModBlocks.BRICK_UNFIRED, drops(Items.CLAY_BALL));
        this.addDrop(ModBlocks.CRUDE_TORCH_UNLIT, drops(ModItems.CRUDE_TORCH_UNLIT));

        this.addDrop(ModBlocks.CRUDE_TORCH_LIT, burningFiniteTorchDrops(ModItems.CRUDE_TORCH_LIT));
        this.addDrop(ModBlocks.CRUDE_TORCH_SMOULDER, burningFiniteTorchDrops(ModItems.CRUDE_TORCH_SMOULDER));
        this.addDrop(ModBlocks.CRUDE_WALL_TORCH_UNLIT, drops(ModItems.CRUDE_TORCH_UNLIT));
        this.addDrop(ModBlocks.CRUDE_WALL_TORCH_LIT, burningFiniteTorchDrops(ModItems.CRUDE_TORCH_LIT));
        this.addDrop(ModBlocks.CRUDE_WALL_TORCH_SMOULDER, burningFiniteTorchDrops(ModItems.CRUDE_TORCH_SMOULDER));

        this.addDrop(ModBlocks.TORCH_UNLIT, drops(ModItems.TORCH_UNLIT));
        this.addDrop(ModBlocks.WALL_TORCH_UNLIT, drops(ModItems.TORCH_UNLIT));
    }

    public LootTable.Builder burningFiniteTorchDrops(ItemConvertible drop) {
        return LootTable.builder().pool(
                this.addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(ItemEntry.builder(drop)
                                .apply(
                                        CopyComponentsLootFunction.builder(
                                                CopyComponentsLootFunction.Source.BLOCK_ENTITY)
                                                .include(ModComponentsTypes.TORCH_FUEL)
                                )
                        )

                )
        );
    }

}