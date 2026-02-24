package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.tag.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public SelfSustainableBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.OVEN_BRICK);

        // Blocks that can ignite items without any specific properties (like LIT or FIRE_LEVEL)
        getOrCreateTagBuilder(ModTags.Blocks.DIRECTLY_IGNITES_ITEM_ON_USE)
                .addTag(ModTags.Blocks.LIT_TORCHES)
                .add(Blocks.LAVA)
                .add(Blocks.FIRE)
                .add(Blocks.SOUL_FIRE);

        getOrCreateTagBuilder(ModTags.Blocks.VANILLA_LIT_TORCHES)
                .add(Blocks.TORCH)
                .add(Blocks.WALL_TORCH)
                .add(Blocks.SOUL_TORCH)
                .add(Blocks.SOUL_WALL_TORCH);

        getOrCreateTagBuilder(ModTags.Blocks.LIT_TORCHES)
                .addTag(ModTags.Blocks.VANILLA_LIT_TORCHES)
                .add(ModBlocks.CRUDE_TORCH_LIT)
                .add(ModBlocks.CRUDE_TORCH_SMOULDER)
                .add(ModBlocks.CRUDE_WALL_TORCH_LIT)
                .add(ModBlocks.CRUDE_WALL_TORCH_SMOULDER);

        getOrCreateTagBuilder(ModTags.Blocks.UNLIT_TORCHES)
                .add(ModBlocks.TORCH_UNLIT)
                .add(ModBlocks.WALL_TORCH_UNLIT)
                .add(ModBlocks.CRUDE_TORCH_UNLIT)
                .add(ModBlocks.CRUDE_WALL_TORCH_UNLIT);
    }
}
