package org.btwr.self_sustainable;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.entity.WickerBasketBE;
import org.btwr.self_sustainable.block.entity.render.*;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.entity.render.HamperItemRenderer;
import org.btwr.self_sustainable.item.entity.render.WickerBasketItemRenderer;
import org.btwr.self_sustainable.network.SyncWickerBasketS2C;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SelfSustainableClient implements ClientModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        ModEntityModelLayers.register();

        this.registerBlockRenderLayers();
        this.registerBlockEntityRenderers();
        this.registerItemRenderers();
        this.registerColorProviders();
        this.registerS2CPackets();
    }

    private void registerBlockRenderLayers() {
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
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SMOKER_BRICK, RenderLayer.getCutout());
    }

    private void registerBlockEntityRenderers() {
        BlockEntityRendererFactories.register(ModBlockEntities.OVEN_BRICK, BrickOvenBERenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.SMOKER_BRICK, SmokeOvenBERenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.CAMPFIRE, CampfireBERenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.WICKER_BASKET, WickerBasketBERenderer::new);
        BlockEntityRendererFactories.register(ModBlockEntities.HAMPER, HamperBERenderer::new);

    }

    private void registerItemRenderers() {
        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.WICKER_BASKET, new WickerBasketItemRenderer());
        BuiltinItemRendererRegistry.INSTANCE.register(ModBlocks.HAMPER, new HamperItemRenderer());
    }

    private void registerColorProviders() {
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            DyeColor color = ModItems.WOOL_KNITS.entrySet().stream()
                    .filter(e -> e.getValue() == stack.getItem())
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(DyeColor.WHITE);
            return 0xFF000000 | color.getFireworkColor();
        }, ModItems.WOOL_KNITS.values().toArray(new Item[0]));

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            DyeColor color = ModItems.WOOLS.entrySet().stream()
                    .filter(e -> e.getValue() == stack.getItem())
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(DyeColor.WHITE);
            return 0xFF000000 | color.getFireworkColor();
        }, ModItems.WOOLS.values().toArray(new Item[0]));

        ColorProviderRegistry.ITEM.register(((stack, tintIndex) -> {
            DyedColorComponent dyedColor = stack.get(DataComponentTypes.DYED_COLOR);
            return dyedColor != null ? 0xFF000000 | dyedColor.rgb() : 0xFFFFFFFF;
        }), ModItems.WOOL_HELMET, ModItems.WOOL_CHESTPLATE, ModItems.WOOL_LEGGINGS, ModItems.WOOL_BOOTS);


        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
            if (tintIndex != 1) return -1;
            DyedColorComponent dyed = stack.get(DataComponentTypes.DYED_COLOR);
            return dyed != null ? 0xFF000000 | dyed.rgb() : 0xFF000000 | DyeColor.WHITE.getFireworkColor();
        }, ModItems.KNITTING);
    }

    private void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SyncWickerBasketS2C.ID, ((payload, context) -> {
            ClientWorld world = context.client().world;

            if (world == null) return;

            BlockEntity blockEntity = world.getBlockEntity(payload.pos());

            if (blockEntity instanceof WickerBasketBE wickerBasketBE) {
                wickerBasketBE.setHeldStacks(payload.inventory());
                world.updateListeners(
                        payload.pos(),
                        blockEntity.getCachedState(),
                        blockEntity.getCachedState(),
                        Block.NOTIFY_ALL_AND_REDRAW
                );
            }
        }));
    }
}