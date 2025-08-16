package net.ivangeevo.self_sustainable.block.entity.renderer;

import net.ivangeevo.self_sustainable.block.blocks.SmokerOvenBlock;
import net.ivangeevo.self_sustainable.block.entity.SmokerOvenBE;
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

public class SmokeOvenBERenderer implements BlockEntityRenderer<SmokerOvenBE>
{

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
        this.renderCookItem(entity, matrices, vertexConsumers);

    }


    private void renderCookItem(SmokerOvenBE ovenBE, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        // Get the itemsBeingCooked from the entity
        ItemStack cookStack = ovenBE.getCookStack();
        Direction facing = ovenBE.getCachedState().get(SmokerOvenBlock.FACING);

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

}
