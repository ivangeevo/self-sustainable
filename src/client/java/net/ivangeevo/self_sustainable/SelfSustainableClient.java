package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.renderer.*;
import net.ivangeevo.self_sustainable.render.ModTexturedRenderLayers;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class SelfSustainableClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.OVEN_BRICK, BrickOvenBERenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMOKER_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModBlockEntities.SMOKER_BRICK, SmokeOvenBERenderer::new);

        BlockEntityRendererFactories.register(ModBlockEntities.CAMPFIRE, CampfireBERenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BRICK_UNFIRED, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.BRICK, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_TORCH_UNLIT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_TORCH_LIT, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_WALL_TORCH_UNLIT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_WALL_TORCH_LIT, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_TORCH_SMOULDER, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_WALL_TORCH_SMOULDER, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_TORCH_BURNED_OUT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.CRUDE_WALL_TORCH_BURNED_OUT, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TORCH_UNLIT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WALL_TORCH_UNLIT, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WICKER_BASKET, ModTexturedRenderLayers.WICKER_BASKET_RENDER_LAYER);

        BlockEntityRendererFactories.register(ModBlockEntities.WICKER_BASKET, WickerBasketBERenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WICKER_BASKET, WickerBasketBERenderer::getTexturedModelData);

    }
}
