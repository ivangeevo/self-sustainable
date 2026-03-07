package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.MatchToolLootCondition;
import net.minecraft.loot.condition.SurvivesExplosionLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ExplosionDecayLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.predicate.item.ItemPredicate;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryWrapper;
import org.btwr.shared_library.api.tag.BTWRConventionalTags;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableLootTableProvider extends FabricBlockLootTableProvider {

    public static final LootCondition.Builder WITH_STONE_AXE = MatchToolLootCondition.builder(ItemPredicate.Builder.create().items(Items.STONE_AXE));
    public static final LootCondition.Builder WITH_MODERN_AXE = MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(BTWRConventionalTags.Items.MODERN_AXES));
    public static final LootCondition.Builder WITH_ADVANCED_AXE = MatchToolLootCondition.builder(ItemPredicate.Builder.create().tag(BTWRConventionalTags.Items.ADVANCED_AXES));


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

        this.addDrop(ModBlocks.SOUL_TORCH_UNLIT, drops(ModItems.SOUL_TORCH_UNLIT));
        this.addDrop(ModBlocks.SOUL_WALL_TORCH_UNLIT, drops(ModItems.SOUL_TORCH_UNLIT));

        this.addDrop(ModBlocks.WICKER_BASKET, dropsForWickerBasket());
        this.addDrop(ModBlocks.HAMPER, dropsForHamper());
    }

    public LootTable.Builder dropsForWickerBasket() {
        AlternativeEntry.Builder alternativeEntry = AlternativeEntry.builder(
                this.silkTouchDropEntry(ModBlocks.WICKER_BASKET),
                ItemEntry.builder(ModBlocks.WICKER_BASKET).conditionally(WITH_ADVANCED_AXE),
                ItemEntry.builder(ModBlocks.WICKER_BASKET).conditionally(WITH_MODERN_AXE),
                ItemEntry.builder(ModBlocks.WICKER_BASKET).conditionally(WITH_STONE_AXE),
                ItemEntry.builder(ModItems.WICKER)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2)))
                        .conditionally(SurvivesExplosionLootCondition.builder())
        );

        return LootTable.builder().pool(
                LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(alternativeEntry)
        );
    }

    public LootTable.Builder dropsForHamper() {
        AlternativeEntry.Builder alternativeEntry = AlternativeEntry.builder(
                this.silkTouchDropEntry(ModBlocks.HAMPER),
                ItemEntry.builder(ModBlocks.HAMPER).conditionally(WITH_ADVANCED_AXE),
                ItemEntry.builder(ModBlocks.HAMPER).conditionally(WITH_MODERN_AXE),
                ItemEntry.builder(ModItems.WICKER)
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(4)))
                        .conditionally(SurvivesExplosionLootCondition.builder())
        );

        return LootTable.builder().pool(
                LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1.0f))
                        .with(alternativeEntry)
        );
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

    LeafEntry.Builder<?> silkTouchDropEntry(Block silkTouchDrop) {
        return ItemEntry.builder(silkTouchDrop).conditionally(this.createSilkTouchCondition());
    }

}