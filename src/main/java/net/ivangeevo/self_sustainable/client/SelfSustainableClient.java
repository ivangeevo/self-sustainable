package net.ivangeevo.self_sustainable.client;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.renderer.BrickOvenBlockEntityRenderer;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class SelfSustainableClient implements ClientModInitializer {


    public static final RenderLayer FUEL_OVERLAY = RenderLayer.getSolid(); // Example custom render layer
    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntityRenderer::new);

        // Associate your block with the custom render layer
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, FUEL_OVERLAY);

    }
}
