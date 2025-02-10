package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.minecraft.item.Items;
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

    }

    @Override
    public String getName() {
        return null;
    }
}
