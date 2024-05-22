package net.ivangeevo.self_sustainable.client;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.renderer.BrickOvenBlockEntityRenderer;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class SelfSustainableClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_TORCH, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_CRUDE_TORCH, RenderLayer.getCutout());







    }
}
