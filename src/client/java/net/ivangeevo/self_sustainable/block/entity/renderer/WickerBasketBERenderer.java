package net.ivangeevo.self_sustainable.block.entity.renderer;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.WickerBasketBlock;
import net.ivangeevo.self_sustainable.block.entity.WickerBasketBE;
import net.ivangeevo.self_sustainable.render.ModTexturedRenderLayers;
import net.minecraft.block.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class WickerBasketBERenderer implements BlockEntityRenderer<WickerBasketBE> {

    private static final String HANDLE = "handle";
    private static final String LID = "lid";
    private static final String LID_BASE = "lid_base";
    private static final String BASKET_BASE = "basket_base";

    // the very top part of the basket
    private final ModelPart handle;
    // the lid top part
    private final ModelPart lid;
    // the base part of the lid
    private final ModelPart lidBase;
    // the bottom part of the basket
    private final ModelPart basketBase;

    public WickerBasketBERenderer(BlockEntityRendererFactory.Context ctx) {

        ModelPart modelPart = ctx.getLayerModelPart(ModEntityModelLayers.WICKER_BASKET);
        this.handle = modelPart.getChild(HANDLE);
        this.lid = modelPart.getChild(LID);
        this.lidBase = modelPart.getChild(LID_BASE);
        this.basketBase = modelPart.getChild(BASKET_BASE);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(HANDLE, ModelPartBuilder.create()
                .uv(0, 19)
                .cuboid(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f), ModelTransform.NONE);
        modelPartData.addChild(LID, ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));
        modelPartData.addChild(LID_BASE, ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(7.0f, -2.0f, 14.0f, 2.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));
        modelPartData.addChild(BASKET_BASE, ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(7.0f, -2.0f, 14.0f, 2.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 9.0f, 1.0f));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(WickerBasketBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();
        boolean hasWorld = world != null;
        BlockState blockState = hasWorld ? entity.getCachedState() : ModBlocks.WICKER_BASKET.getDefaultState();
        Block block = blockState.getBlock();

        if (!(block instanceof WickerBasketBlock)) {
            return;
        }

        float rotation = blockState.get(WickerBasketBlock.FACING).asRotation();
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
        matrices.translate(-0.5f, -0.5f, -0.5f);

        float lidProgress = entity.getAnimationProgress(tickDelta);
        lidProgress = 1.0f - lidProgress;
        lidProgress = 1.0f - lidProgress * lidProgress * lidProgress;

        SpriteIdentifier texture = ModTexturedRenderLayers.getWickerBasket();
        VertexConsumer vertexConsumer = texture.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);

        this.render(matrices, vertexConsumer, this.lid, this.lidBase, this.handle, this.basketBase, lidProgress, light, overlay);

        matrices.pop();
    }

    private void render(MatrixStack matrices, VertexConsumer vertices, ModelPart lid,  ModelPart lidBase, ModelPart latch, ModelPart base, float openFactor, int light, int overlay) {
        latch.pitch = lid.pitch = -(openFactor * 1.5707964f);
        lid.render(matrices, vertices, light, overlay);
        lidBase.render(matrices, vertices, light, overlay);
        latch.render(matrices, vertices, light, overlay);
        base.render(matrices, vertices, light, overlay);
    }


}
