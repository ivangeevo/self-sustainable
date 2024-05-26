package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.CampfireBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntityRenderer.class)
public abstract class CampfireBlockEntityRendererMixin {

    @Shadow @Final private ItemRenderer itemRenderer;

    @Inject(method = "render(Lnet/minecraft/block/entity/CampfireBlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)V", at = @At("HEAD"), cancellable = true)
    private void injectedCustomRender(CampfireBlockEntity campfireBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, CallbackInfo ci) {
        this.renderCookItem(campfireBlockEntity, matrixStack, vertexConsumerProvider, i, j);
        ci.cancel();
    }

    @Unique
    private void renderCookItem(CampfireBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        // Get the itemsBeingCooked from the entity
        DefaultedList<ItemStack> itemsBeingCooked = entity.getItemsBeingCooked();

        if (!itemsBeingCooked.isEmpty()) {
            ItemStack cookStack = itemsBeingCooked.get(0); // Take the first item

            Direction facing = entity.getCachedState().get(CampfireBlock.FACING);

            matrices.push();

            // Move to the center of the block and adjust the height to be 0.9
            matrices.translate(0.5f, 1.0f, 0.5f);

            // Rotate based on the facing direction
            RotationAxis rotationAxis = getRotationAxis(facing);
            matrices.multiply(rotationAxis.rotationDegrees(facing.asRotation()));

            // Scale the item to an appropriate size
            matrices.scale(0.5f, 0.5f, 0.5f);

            // Use the itemRenderer to render the item
            this.itemRenderer.renderItem(cookStack, ModelTransformationMode.GUI,
                    LightmapTextureManager.pack(8, 15), OverlayTexture.DEFAULT_UV,
                    matrices, vertexConsumers, entity.getWorld(), 1);

            matrices.pop();
        }
    }

    // Get RotationAxis based on facing direction
    @Unique
    private RotationAxis getRotationAxis(Direction facing) {
        return RotationAxis.POSITIVE_Y;
    }
}
