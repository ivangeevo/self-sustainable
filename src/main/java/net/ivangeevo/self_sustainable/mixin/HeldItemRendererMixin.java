package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin
{

    // For progressive crafting
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"))
    private void injectRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (item.getItem().getCustomUseAction() == CustomUseAction.PROGRESSIVE_CRAFT)
        {
            float useCount = (float) player.getItemUseTimeLeft() - tickDelta + 1.0F;
            float durationRatio = useCount / (float) item.getMaxUseTime(player);
            float inverseRatio = 1.0F - durationRatio;

            inverseRatio = inverseRatio * inverseRatio * inverseRatio;
            inverseRatio = inverseRatio * inverseRatio * inverseRatio;
            inverseRatio = inverseRatio * inverseRatio * inverseRatio;

            float finalRatio = 1.0F - inverseRatio;

            matrices.translate(0.0F, MathHelper.abs(MathHelper.cos(useCount / 4.0F * (float) Math.PI) * 0.1F) * (durationRatio > 0.2D ? 1 : 0), 0.0F);
            matrices.translate(finalRatio * 0.6F, -finalRatio * 0.5F, 0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(finalRatio * 90.0F));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(finalRatio * 10.0F));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(finalRatio * 30.0F));
        }
    }
}
