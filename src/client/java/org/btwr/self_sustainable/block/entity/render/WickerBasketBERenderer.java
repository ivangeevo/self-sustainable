package org.btwr.self_sustainable.block.entity.render;

import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.btwr.self_sustainable.block.entity.WickerBasketBE;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.entity.render.model.WickerBasketModel;

import java.util.ArrayList;
import java.util.List;

public class WickerBasketBERenderer implements BlockEntityRenderer<WickerBasketBE> {

    private static final List<ItemTransformation> TRANSFORMATIONS = new ArrayList<>();

    static {
        TRANSFORMATIONS.add(new ItemTransformation(0,0,0)); // No transform
        TRANSFORMATIONS.add(new ItemTransformation(0.01, 0.04, -0.1)); // 2 items
        TRANSFORMATIONS.add(new ItemTransformation(-0.06, -0.04, 0.2)); // 3 items
        TRANSFORMATIONS.add(new ItemTransformation(0.03, 0.01, 0.1)); // 4 items
    }

    private final BlockEntityRendererFactory.Context context;
    private final WickerBasketModel model;

    public WickerBasketBERenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
        this.model = new WickerBasketModel(context.getLayerModelPart(WickerBasketModel.LAYER_LOCATION));
    }

    @Override
    public void render(WickerBasketBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();

        Direction direction = entity.getHorizontalFacing();
        matrices.translate(0.5F, 0.0F, 0.5F);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-direction.asRotation()));
        float maxAngle = (float) Math.toRadians(38);
        ModelPart lid = this.model.getLid();

        float g = entity.getAnimationProgress(tickDelta);
        g = 1F - g;
        g = 1F - g * g * g;

        lid.pitch = -g * maxAngle;

        if (lid.pitch > 0.01D) {
            DefaultedList<ItemStack> inventory = entity.getHeldStacks();
            World world = entity.getWorld();

            double x = 0.2D, z = 0.2D;

            matrices.push();

            for (ItemStack stack : inventory) {
                boolean isBlock = stack.getItem() instanceof BlockItem;
                float scale = isBlock ? 0.5F : 0.4F;
                int duplicates = Math.clamp(stack.getCount() / Math.max(stack.getMaxCount() / 4, 1) + 1, 1, 4);

                matrices.push();
                matrices.translate(x, 0.25F + 0.15F * (lid.pitch / maxAngle), z);
                matrices.scale(scale, scale, scale);

                for (int count = 0; count < duplicates; count++) {
                    if (stack.isEmpty()) continue;

                    matrices.push();
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25F));
                    matrices.translate(TRANSFORMATIONS.get(count).x, TRANSFORMATIONS.get(count).y, TRANSFORMATIONS.get(count).z);

                    this.context.getItemRenderer().renderItem(
                            stack, ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, world, 0
                    );

                    matrices.pop();
                }

                x -= 0.4D;

                if (x < -0.2D) {
                    z -= 0.4D;
                    x = 0.2D;
                }

                matrices.pop();
            }

            matrices.pop();
        }

        VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(WickerBasketModel.TEXTURE_LOCATION));

        this.model.render(matrices, buffer, light, overlay, -1);
        matrices.pop();
    }

    public record ItemTransformation(double x, double y, double z) {}

}