package net.ivangeevo.selfsustainable.block.entity.renderer;

import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
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

        // Get the itemsBeingCooked from the entity
        DefaultedList<ItemStack> itemsBeingCooked = entity.getItemsBeingCooked();
        Direction facing = entity.getCachedState().get(BrickOvenBlock.FACING);

        float yawDegrees = facing.asRotation();

        matrices.push();
        matrices.translate(0.5f, 0.6f, 0.7f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yawDegrees));
        matrices.scale(0.35f, 0.35f, 0.35f);

        // Use the itemRenderer to render the item
        this.itemRenderer.renderItem(itemsBeingCooked.get(0), ModelTransformationMode.GUI,
                LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
                matrices, vertexConsumers, entity.getWorld(), 1);

        matrices.pop();

    }


}
