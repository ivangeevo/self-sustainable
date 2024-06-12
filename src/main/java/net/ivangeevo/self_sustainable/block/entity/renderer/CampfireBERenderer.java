/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ivangeevo.self_sustainable.block.entity.VariableCampfireBE;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Unique;

@Environment(value=EnvType.CLIENT)
public class CampfireBERenderer
        implements BlockEntityRenderer<VariableCampfireBE> {
    private static final float SCALE = 0.375f;
    private final ItemRenderer itemRenderer;

    public CampfireBERenderer(BlockEntityRendererFactory.Context ctx) {
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(VariableCampfireBE campfireBE, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        // Get the itemsBeingCooked from the entity
        DefaultedList<ItemStack> itemsBeingCooked = campfireBE.getItemsBeingCooked();

        if (!itemsBeingCooked.isEmpty()) {
            ItemStack cookStack = itemsBeingCooked.get(0); // Take the first item

            Direction facing = campfireBE.getCachedState().get(CampfireBlock.FACING);

            matrixStack.push();

            // Move to the center of the block and adjust the height to be 0.9
            matrixStack.translate(0.5f, 1.0f, 0.5f);

            // Rotate based on the facing direction
            RotationAxis rotationAxis = getRotationAxis(facing);
            matrixStack.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

            // Scale the item to an appropriate size
            matrixStack.scale(0.5f, 0.5f, 0.5f);

            // Use the itemRenderer to render the item
            this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                    LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
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

