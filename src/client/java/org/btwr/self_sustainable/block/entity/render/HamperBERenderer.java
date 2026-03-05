package org.btwr.self_sustainable.block.entity.render;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.entity.HamperBE;
import org.btwr.self_sustainable.block.entity.WickerBasketBE;
import org.btwr.self_sustainable.block.entity.render.model.HamperModel;
import org.btwr.self_sustainable.block.entity.render.model.WickerBasketModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted directly from Primitive Storage (CC0).
 *
 * <p>Original project:
 * <a href="https://github.com/jeffinitup/primitive-storage/">
 * https://github.com/jeffinitup/primitive-storage/
 * </a>
 *
 * <p>Original author:
 * JeffyJamzHD
 *
 */
public class HamperBERenderer implements BlockEntityRenderer<HamperBE> {

    private final BlockEntityRendererFactory.Context context;
    private final HamperModel model;

    public HamperBERenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
        this.model = new HamperModel(context.getLayerModelPart(HamperModel.LAYER_LOCATION));
    }

    @Override
    public void render(HamperBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        Direction direction = entity.getHorizontalFacing();
        matrices.translate(0.5F, 1.5F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180F));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(direction.asRotation()));

        float maxAngle = (float) Math.toRadians(-37.5D);

        ModelPart lid = this.model.getLid();

        float g = entity.getAnimationProgress(tickDelta);

        g = 1F - g;
        g = 1F - g * g * g;

        lid.pitch = g * maxAngle;

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(HamperModel.TEXTURE_LOCATION));

        this.model.render(matrices, buffer, light, overlay, -1);
        matrices.pop();
    }

}