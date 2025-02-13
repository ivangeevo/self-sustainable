package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.ModItems;
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
        this.addDrop(ModBlocks.TORCH_UNLIT, drops(ModItems.TORCH_UNLIT));
        this.addDrop(ModBlocks.TORCH_LIT, drops(ModItems.TORCH_LIT));
        this.addDrop(ModBlocks.TORCH_SMOULDER, drops(ModItems.TORCH_SMOULDER));
        this.addDrop(ModBlocks.WALL_TORCH_UNLIT, drops(ModItems.TORCH_UNLIT));
        this.addDrop(ModBlocks.WALL_TORCH_LIT, drops(ModItems.TORCH_LIT));
        this.addDrop(ModBlocks.WALL_TORCH_SMOULDER, drops(ModItems.TORCH_SMOULDER));

    }

    @Override
    public String getName() {
        return null;
    }
}
