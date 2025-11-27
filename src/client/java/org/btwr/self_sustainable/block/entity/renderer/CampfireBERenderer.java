package org.btwr.self_sustainable.block.entity.renderer;

import org.btwr.self_sustainable.block.entity.VariableCampfireBE;
import net.minecraft.block.CampfireBlock;
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
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public class CampfireBERenderer
        implements BlockEntityRenderer<VariableCampfireBE> {

    private final ItemRenderer itemRenderer;

    public CampfireBERenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(VariableCampfireBE campfireBE, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        // Get the itemsBeingCooked from the entity
        DefaultedList<ItemStack> itemsBeingCooked = campfireBE.getItemsBeingCooked();

        if (!itemsBeingCooked.isEmpty()) {
            ItemStack cookStack = itemsBeingCooked.getFirst();

            Direction facing = campfireBE.getCachedState().get(CampfireBlock.FACING);

            matrixStack.push();

            // Move to the center of the block and adjust the height to be 0.9
            matrixStack.translate(0.5f, 1.0f, 0.5f);

            // Rotate based on the facing direction
            RotationAxis rotationAxis = getRotationAxis(facing);
            matrixStack.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

            // Scale the item to an appropriate size
            matrixStack.scale(0.5f, 0.5f, 0.5f);

            // Get the BlockPos at the campfire
            BlockPos blockPos = campfireBE.getPos();
            World world = campfireBE.getWorld();

            assert world != null;
            int blockLight = world.getLightLevel(LightType.BLOCK, blockPos);
            int skyLight = world.getLightLevel(LightType.SKY, blockPos);

            int lightPacked = LightmapTextureManager.pack(blockLight, skyLight);

            // Use the itemRenderer to render the item
            this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                    lightPacked, OverlayTexture.DEFAULT_UV,
                    matrixStack, vertexConsumerProvider, campfireBE.getWorld(), 1);

            matrixStack.pop();
        }
    }

    // Get RotationAxis based on facing direction
    @Unique
    private RotationAxis getRotationAxis(Direction facing) {
        return RotationAxis.POSITIVE_Y;
    }

}