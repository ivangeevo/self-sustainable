package ivangeevo.selfsustainable.client;

import ivangeevo.selfsustainable.ModItems;
import ivangeevo.selfsustainable.block.ModBlocks;
import ivangeevo.selfsustainable.block.entity.renderer.BrickOvenBlockEntityRenderer;
import ivangeevo.selfsustainable.block.entity.renderer.StoneOvenBlockEntityRenderer;
import ivangeevo.selfsustainable.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public class SelfSustainableClient implements ClientModInitializer {



    @Override
    public void onInitializeClient() {

// Register the main knitting needles model
        ModelPredicateProviderRegistry.register(ModItems.KNITTING, new Identifier("has_white_wool"), (stack, world, player, provider) -> {
            int color = stack.getOrCreateNbt().getInt("color");
            return color == DyeColor.WHITE.getId() ? 1.0F : 0;
        });

        // Register models for each wool color
        for (DyeColor dyeColor : DyeColor.values()) {
            if (dyeColor != DyeColor.WHITE && dyeColor != DyeColor.LIGHT_GRAY) {
                Identifier woolItem = new Identifier("btwr:" + dyeColor.getName() + "_wool");
                Identifier predicateKey = new Identifier("has_" + dyeColor.getName() + "_wool");

                ModelPredicateProviderRegistry.register(ModItems.KNITTING, predicateKey, (stack, world, player, provider) -> {
                    int color = stack.getOrCreateNbt().getInt("color");
                    return color == dyeColor.getId() ? 1.0F : 0;
                });
            }
        }

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (stack.getItem() instanceof WoolItem) {
                        return ((WoolItem)stack.getItem()).getColor(stack, tintIndex);
                    }
                    return -1; // Return -1 for default color if the item is not a WoolItem
                },
                ModItems.WHITE_WOOL, ModItems.ORANGE_WOOL, ModItems.MAGENTA_WOOL,
                ModItems.LIGHT_BLUE_WOOL, ModItems.YELLOW_WOOL, ModItems.LIME_WOOL,
                ModItems.PINK_WOOL, ModItems.GRAY_WOOL, ModItems.LIGHT_GRAY_WOOL,
                ModItems.CYAN_WOOL, ModItems.PURPLE_WOOL, ModItems.BLUE_WOOL,
                ModItems.BROWN_WOOL, ModItems.GREEN_WOOL, ModItems.RED_WOOL,
                ModItems.BLACK_WOOL);


        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
                    if (stack.getItem() instanceof WoolKnitItem) {
                        return ((WoolKnitItem)stack.getItem()).getColor(stack, tintIndex);
                    }
                    return -1; // Return -1 for default color if the item is not a WoolItem
                },
                ModItems.WOOL_KNIT);


        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_STONE, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModEntities.Blocks.OVEN_STONE, StoneOvenBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.OVEN_BRICK, RenderLayer.getCutout());
        BlockEntityRendererFactories.register(ModEntities.Blocks.OVEN_BRICK, BrickOvenBlockEntityRenderer::new);


    }
}
