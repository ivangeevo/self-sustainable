package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{

    @Shadow public abstract TextRenderer getTextRenderer();

    @Shadow private int scaledWidth;

    @Shadow private int scaledHeight;

    @Inject(method = "render", at = @At("HEAD"))
    private void injectedRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player != null) {
            HungerManager hungerManager = player.getHungerManager();
            int foodLevel = hungerManager.getFoodLevel();
            int healthLevel = (int) player.getHealth();

            String foodStatus = "";
            String healthStatus = "";

            switch (foodLevel)
            {
                case 0,1:
                    foodStatus = "Starving";
                    break;
                case 2,3:
                    foodStatus = "Famished";
                    break;
                case 4,5,6:
                    foodStatus = "Hungry";
                    break;
                case 7,8:
                    foodStatus = "Peckish";
                    break;
                default:
                    // No specific food status for food levels above 8
                    break;
            }

            switch (healthLevel)
            {
                case 0,1,2:
                    healthStatus = "Dying";
                    break;
                case 3,4:
                    healthStatus = "Crippled";
                    break;
                case 5,6:
                    healthStatus = "Wounded";
                    break;
                case 7,8:
                    healthStatus = "Injured";
                    break;
                case 9,10:
                    healthStatus = "Hurt";
                    break;
                default:
                    // No specific health status for health levels above 10
                    break;
            }

            if (!healthStatus.isEmpty())
            {
                renderWellbeingStatusText(context, healthStatus);
            }
            else if (!foodStatus.isEmpty())
            {
                renderWellbeingStatusText(context, foodStatus);
            }
        }
    }


    @Unique
    private void renderWellbeingStatusText(DrawContext context, String text) {
        TextRenderer textRenderer = getTextRenderer();
        Text statusText = Text.translatable(text);

        // Calculate the position of the hunger bar
        int hungerBarX = this.scaledWidth / 2 + 91;
        int hungerBarY = this.scaledHeight - 39;

        // Adjust the X and Y positions to render above the hunger bar
        int textX = hungerBarX - (textRenderer.getWidth(statusText) / 2) - 20; // 10 pixels to the left of the hunger bar
        int textY = hungerBarY - 10; // Adjust the Y position to be above the hunger bar

        // Draw the status text
        textRenderer.draw(statusText.getString(), textX, textY, 0xFFFFFFFF, true, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0, textRenderer.isRightToLeft());
    }

}
