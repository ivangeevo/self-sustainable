package net.ivangeevo.self_sustainable.block.entity.renderer;

import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class BrickOvenBlockEntityRenderer implements BlockEntityRenderer<BrickOvenBlockEntity> {

    private final ItemRenderer itemRenderer;


    public BrickOvenBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }


    // TODO: Fix the item model orientation.
    // It displays with a wrong orientation for different directions.
    @Override
    public void render(BrickOvenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Render the fuel level overlay as a whole block
        /** Done as Blockstates currently **/
       // this.renderFuelLevelOverlay(entity, matrices, vertexConsumers, light, overlay);

        // Render the item being in the oven
        this.renderCookItem(entity, matrices, vertexConsumers, light, overlay);

    }


    private void renderCookItem(BrickOvenBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Get the itemsBeingCooked from the entity
        DefaultedList<ItemStack> itemsBeingCooked = entity.getCookStack();
        Direction facing = entity.getCachedState().get(BrickOvenBlock.FACING);

        matrices.push();

        // Move to the center of the block
        matrices.translate(0.5f, 0.6f, 0.5f);

        // Integrate the visual offset logic
        applyVisualOffset(matrices, facing);

        // Rotate based on the facing direction
        RotationAxis rotationAxis = getRotationAxis(facing);
        matrices.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

        // Scale the item to an appropriate size
        matrices.scale(0.35f, 0.35f, 0.35f);

        // Use the itemRenderer to render the item
        this.itemRenderer.renderItem(itemsBeingCooked.get(0), ModelTransformationMode.GUI,
                LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), 1);

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


    // TODO: FIX THE RENDERING OF THE FUEL OVERLAY OR DO IT IN BLOCKSTATES WITH DIFFERENT MODELS
/**
    private void renderFuelLevelOverlay(BrickOvenBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        boolean shouldDisplay = entity.getCachedState().get(ModProperties.HAS_FUEL)
                && entity.getCachedState().get(Properties.LIT);

        if (shouldDisplay) {
            matrices.push();


            Direction facing = entity.getCachedState().get(BrickOvenBlock.FACING);

            int fuelLevel = entity.getVisualFuelLevel();

            BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
            BlockModelRenderer blockModelRenderer = blockRenderManager.getModelRenderer();

            // Use your custom block entry instead of block state
            BlockState blockState = entity.getCachedState().with(ModProperties.FUEL_LEVEL, fuelLevel);


            int blockLight = WorldRenderer.getLightmapCoordinates(Objects.requireNonNull(entity.getWorld()), entity.getPos().offset(facing));
            int blockOverlay = OverlayTexture.DEFAULT_UV;

            // Get the baked model for your custom block entry
            BakedModel bakedModel = blockRenderManager.getModel(blockState);

            // Render the baked model using BlockModelRenderer
            blockModelRenderer.render(matrices.peek(), vertexConsumers.getBuffer(RenderLayer.getCutout()), blockState,
                    bakedModel, 1.0f, 1.0f, 1.0f, blockLight, blockOverlay);

            matrices.pop();
        }
    }
 **/


}
