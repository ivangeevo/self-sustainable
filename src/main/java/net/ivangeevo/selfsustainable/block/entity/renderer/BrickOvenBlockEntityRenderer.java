package net.ivangeevo.selfsustainable.block.entity.renderer;

import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;

public class BrickOvenBlockEntityRenderer implements BlockEntityRenderer<BrickOvenBlockEntity> {

    private final ItemRenderer itemRenderer;

    public BrickOvenBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(BrickOvenBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = entity.getCachedState().get(BrickOvenBlock.FACING);

        // Get the itemsBeingCooked from the entity (similar to CampfireBlockEntity)
        DefaultedList<ItemStack> itemsBeingCooked = entity.getItemsBeingCooked();

        matrices.push();

        matrices.translate(0.5f, 0.6f, 0.5f);
        float yawDegrees = -facing.asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yawDegrees));

        // Use the logic from CampfireBlockEntityRenderer for rendering items
        float scale = 0.375f;

        for (ItemStack itemStack : itemsBeingCooked) {
            if (itemStack.isEmpty()) continue;

            matrices.push();

            matrices.scale(scale, scale, scale);

            // Use the itemRenderer to render the item
            itemRenderer.renderItem(itemStack, ModelTransformationMode.GUI, manuallySetLightLevel(entity.getWorld(),
                    entity.getPos()), OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, entity.getWorld(), 1);
            matrices.pop();
        }

        matrices.pop();
    }

    private int manuallySetLightLevel(World world, BlockPos pos) {
        // Return a constant light level of 8
        return LightmapTextureManager.pack(8, 15);
    }


}
