package org.btwr.self_sustainable.block.entity.render;

import org.btwr.self_sustainable.block.blocks.SmokerOvenBlock;
import org.btwr.self_sustainable.block.entity.SmokerOvenBE;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SmokeOvenBERenderer implements BlockEntityRenderer<SmokerOvenBE> {

    private final ItemRenderer itemRenderer;

    public SmokeOvenBERenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    // TODO: Fix the item model orientation.
    // It displays with a wrong orientation for different directions.
    @Override
    public void render(SmokerOvenBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Render the fuel level overlay as a whole block
        /** Done as Blockstates currently **/
       // this.renderFuelLevelOverlay(entity, matrices, vertexConsumers, light, overlay);

        // Render the item being in the oven
        //this.renderCookItem(entity, matrices, vertexConsumers);
    }



}