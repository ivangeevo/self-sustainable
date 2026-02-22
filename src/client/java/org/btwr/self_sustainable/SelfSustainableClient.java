package org.btwr.self_sustainable;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.entity.renderer.*;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.render.ModTexturedRenderLayers;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

import java.util.Map;

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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_TORCH_UNLIT, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SOUL_WALL_TORCH_UNLIT, RenderLayer.getCutout());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WICKER_BASKET, ModTexturedRenderLayers.WICKER_BASKET_RENDER_LAYER);

        BlockEntityRendererFactories.register(ModBlockEntities.WICKER_BASKET, WickerBasketBERenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ModEntityModelLayers.WICKER_BASKET, WickerBasketBERenderer::getTexturedModelData);

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            DyeColor color = ModItems.WOOL_KNITS.entrySet().stream()
                    .filter(e -> e.getValue() == stack.getItem())
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(DyeColor.WHITE);
            return 0xFF000000 | color.getFireworkColor();
        }, ModItems.WOOL_KNITS.values().toArray(new Item[0]));

        ColorProviderRegistry.ITEM.register(((stack, tintIndex) -> {
            DyedColorComponent dyedColor = stack.get(DataComponentTypes.DYED_COLOR);
            return dyedColor != null ? 0xFF000000 | dyedColor.rgb() : 0xFFFFFFFF;
        }), ModItems.WOOL_HELMET, ModItems.WOOL_CHESTPLATE, ModItems.WOOL_LEGGINGS, ModItems.WOOL_BOOTS);

    }
}