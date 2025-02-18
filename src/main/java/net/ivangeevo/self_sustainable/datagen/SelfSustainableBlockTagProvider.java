package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class SelfSustainableBlockTagProvider extends FabricTagProvider.BlockTagProvider
{


    public SelfSustainableBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg)
    {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.OVEN_BRICK);

        getOrCreateTagBuilder(ModTags.Blocks.DIRECTLY_IGNITABLE_FROM_ON_USE)
                .add(Blocks.TORCH)
                .add(Blocks.SOUL_TORCH)
                .add(Blocks.CAMPFIRE)
                .add(Blocks.SOUL_CAMPFIRE)
                .add(Blocks.LAVA)

                .add(ModBlocks.CRUDE_TORCH_LIT)
                .add(ModBlocks.CRUDE_WALL_TORCH_LIT)
                .add(ModBlocks.CRUDE_TORCH_SMOULDER)
                .add(ModBlocks.CRUDE_WALL_TORCH_SMOULDER)

                .add(ModBlocks.OVEN_BRICK)
                .add(ModBlocks.SMOKER_BRICK)
        ;



    }
}
