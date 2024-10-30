package net.ivangeevo.self_sustainable.block.entity.renderer;

import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.entity.BrickOvenBE;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class BrickOvenBERenderer implements BlockEntityRenderer<BrickOvenBE> {

    private final ItemRenderer itemRenderer;

    public BrickOvenBERenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }


    // TODO: Fix the item model orientation.

    // TODO:HELP: But more importantly fix rendering the cookStack since 1.20.5-1.21 changes. I belive the error might originate
    //  from the cook stack not being correct in the Block entity (BrickOvenBE)
    // It displays with a wrong orientation for different directions.
    @Override
    public void render(BrickOvenBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Render the fuel level overlay as a whole block
        /** Done as Blockstates currently **/
       // this.renderFuelLevelOverlay(entity, matrices, vertexConsumers, light, overlay);

        // Render the item being in the oven
        this.renderCookItem(entity, matrices, vertexConsumers);

    }

    /**     private void renderCookItem(BrickOvenBE ovenBE, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
     // Get the item being cooked from the entity
     ItemStack cookStack = ovenBE.getCookStack();
     Direction facing = ovenBE.getCachedState().get(BrickOvenBlock.FACING);

     if (cookStack.isEmpty()) return;

     matrices.push();

     // Move to the center of the block
     matrices.translate(0.5f, 0.58f, 0.5f);

     // Apply facing-based rotation
     float rotationAngle = -facing.asRotation();
     matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationAngle));
     matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));  // Align item flat

     // Scale the item to an appropriate size
     matrices.scale(0.35f, 0.35f, 0.35f);

     // Render the item with fixed transformation mode
     this.itemRenderer.renderItem(cookStack, ModelTransformationMode.FIXED,
     light, overlay, matrices, vertexConsumers, ovenBE.getWorld(), 0);

     matrices.pop();
     }
 **/


     private void renderCookItem(BrickOvenBE ovenBE, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        // Get the itemsBeingCooked from the entity
        ItemStack cookStack = ovenBE.getCookStack();
        Direction facing = ovenBE.getCachedState().get(BrickOvenBlock.FACING);

        matrices.push();

        // Move to the center of the block
        matrices.translate(0.5f, 0.58f, 0.5f);

        // Integrate the visual offset logic
        applyVisualOffset(matrices, facing);

        // Rotate based on the facing direction
        RotationAxis rotationAxis = this.getRotationAxis(facing);
        matrices.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

        // Scale the item to an appropriate size
        matrices.scale(0.35f, 0.35f, 0.35f);

        // Use the itemRenderer to render the item
        this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, ovenBE.getWorld(), 1);

        matrices.pop();
    }


    // Get RotationAxis based on facing direction
    private RotationAxis getRotationAxis(Direction facing) {
        return switch (facing) {
            case NORTH -> RotationAxis.POSITIVE_Z;
            case SOUTH -> RotationAxis.POSITIVE_Z;
            case WEST -> RotationAxis.POSITIVE_Y;
            case EAST -> RotationAxis.POSITIVE_Y;
            default -> RotationAxis.POSITIVE_Y; // Default to Y-axis if facing direction is not recognized
        };
    }

    private void applyVisualOffset(MatrixStack matrices, Direction facing) {
        float visualOffset = 0.25f;

        switch (facing) {
            case NORTH:
                matrices.translate(0.0f, 0.0f, -0.5f + visualOffset);
                break;
            case SOUTH:
                matrices.translate(0.0f, 0.0f, 0.5f - visualOffset);
                break;
            case WEST:
                matrices.translate(-0.5f + visualOffset, 0.0f, 0.0f);
                break;
            case EAST:
                matrices.translate(0.5f - visualOffset, 0.0f, 0.0f);
                break;
        }
    }

}
