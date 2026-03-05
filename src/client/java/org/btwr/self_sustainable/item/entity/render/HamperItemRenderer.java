package org.btwr.self_sustainable.item.entity.render;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;
import org.btwr.self_sustainable.block.entity.render.model.HamperModel;
import org.btwr.self_sustainable.block.entity.render.model.WickerBasketModel;

public class HamperItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

    private final ModelPart hamper;

    public HamperItemRenderer() {
        this.hamper = HamperModel.getTexturedModelData().createModel();
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180F));

        ModelPart lid = this.hamper.getChild("root").getChild("lid");

        lid.pitch = 0f;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(HamperModel.TEXTURE_LOCATION));
        hamper.render(matrices, buffer, light, overlay);

        matrices.pop();
    }
}
