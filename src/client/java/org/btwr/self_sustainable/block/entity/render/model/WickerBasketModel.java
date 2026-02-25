package org.btwr.self_sustainable.block.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;

public class WickerBasketModel extends Model {

    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(IdUtils.ofSS("wicker_basket"), "main");
    public static final Identifier TEXTURE_LOCATION = IdUtils.ofSS("textures/entity/wicker_basket.png");

    private final ModelPart root;
    private final ModelPart lid;
    private final ModelPart body;

    public WickerBasketModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);

        this.root = root.getChild("root");
        this.lid = this.root.getChild("lid");
        this.body = this.root.getChild("body");
    }


    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        root.render(matrices, vertexConsumer, light, overlay, -1);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData lid = root.addChild("lid", ModelPartBuilder.create().uv(44, 28).cuboid(-2.0F, 1.5F, 5.0F, 4.0F, 1.0F, 2.0F, new Dilation(0.0F))
                .uv(0, 15).cuboid(-7.0F, 0.5F, 0.0F, 14.0F, 1.0F, 12.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-8.0F, -0.5F, -1.0F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 7.5F, -6.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create().uv(0, 28).cuboid(-6.0F, 0.0F, -5.0F, 12.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(22, 39).cuboid(-7.0F, 0.0F, -6.0F, 14.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(22, 39).cuboid(-7.0F, 0.0F, 5.0F, 14.0F, 7.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(6.0F, 0.0F, -5.0F, 1.0F, 7.0F, 10.0F, new Dilation(0.0F))
                .uv(0, 39).cuboid(-7.0F, 0.0F, -5.0F, 1.0F, 7.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public ModelPart getLid() {
        return this.lid;
    }

}
