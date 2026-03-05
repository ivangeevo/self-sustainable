package org.btwr.self_sustainable.block.entity.render.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;

public class HamperModel extends Model {

    public static final EntityModelLayer LAYER_LOCATION = new EntityModelLayer(IdUtils.ofSS("hamper"), "main");
    public static final Identifier TEXTURE_LOCATION = IdUtils.ofSS("textures/entity/hamper.png");

    private final ModelPart root;
    private final ModelPart lid;
    private final ModelPart body;

    public HamperModel(ModelPart root) {
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

        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData lid = root.addChild("lid", ModelPartBuilder.create()
                .uv(20, 21).cuboid(-6.0F, -3.0F, -11.0F, 12.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(5, 4).cuboid(-7.0F, -2.0F, -12.0F, 14.0F, 1.0F, 12.0F, new Dilation(0.0F))
                .uv(2, 2).cuboid(-8.0F, -1.0F, -13.0F, 16.0F, 1.0F, 14.0F, new Dilation(0.0F)), ModelTransform.of(0.0F, -13.0F, 6.0F, -0.6545F, 0.0F, 0.0F));

        ModelPartData body = root.addChild("body", ModelPartBuilder.create()
                .uv(5, 18).cuboid(-7.0F, -13.0F, -6.0F, 14.0F, 11.0F, 12.0F, new Dilation(0.0F))
                .uv(20, 20).cuboid(-6.0F, -2.0F, -5.0F, 12.0F, 2.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    public ModelPart getLid() {
        return this.lid;
    }

}
