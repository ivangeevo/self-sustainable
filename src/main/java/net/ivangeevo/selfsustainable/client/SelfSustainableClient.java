package net.ivangeevo.selfsustainable.client;

import net.ivangeevo.selfsustainable.block.ModBlocks;
import net.ivangeevo.selfsustainable.block.entity.renderer.BrickOvenBlockEntityRenderer;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class SelfSustainableClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntityRenderer::new);

    }
}
