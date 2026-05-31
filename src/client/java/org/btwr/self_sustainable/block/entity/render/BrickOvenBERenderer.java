package org.btwr.self_sustainable.block.entity.render;

import net.minecraft.client.render.WorldRenderer;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.block.entity.BrickOvenBE;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class BrickOvenBERenderer implements BlockEntityRenderer<BrickOvenBE> {

    private final ItemRenderer itemRenderer;

    public BrickOvenBERenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(BrickOvenBE entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        // Render the item being in the oven
        this.renderCookItem(entity, matrices, vertexConsumers);
    }

    private void renderCookItem(@NotNull BrickOvenBE ovenBE, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        ItemStack cookStack = ovenBE.getCookStack();
        Direction facing = ovenBE.getCachedState().get(BrickOvenBlock.FACING);

        if (cookStack.isEmpty()) return;

        matrices.push();

        // Center of block, slightly upwards
        matrices.translate(0.5f, 0.58f, 0.5f);

        // Offset by facing direction
        applyVisualOffset(matrices, facing);

        // Rotate around Y so it matches the oven's direction
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        // Scale the item
        matrices.scale(0.35f, 0.35f, 0.35f);

        // Get the BlockPos in front of the oven
        World world = ovenBE.getWorld();

        assert world != null;

        // Sample lighting from the oven opening.
        BlockPos lightPos = ovenBE.getPos().offset(facing);
        int mixedLight = WorldRenderer.getLightmapCoordinates(world, lightPos);

        this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                mixedLight, OverlayTexture.DEFAULT_UV,
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

}