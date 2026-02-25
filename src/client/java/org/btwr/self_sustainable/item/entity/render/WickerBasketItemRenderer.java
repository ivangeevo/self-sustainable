package org.btwr.self_sustainable.item.entity.render;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.btwr.self_sustainable.block.entity.render.model.WickerBasketModel;

public class WickerBasketItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    private final ModelPart wickerBasket;

    public WickerBasketItemRenderer() {
        this.wickerBasket = WickerBasketModel.getTexturedModelData().createModel();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);

        VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WickerBasketModel.TEXTURE_LOCATION));
        wickerBasket.render(matrices, vertices, light, overlay);

        matrices.pop();
    }
}
