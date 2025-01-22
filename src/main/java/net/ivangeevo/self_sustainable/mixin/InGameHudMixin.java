package net.ivangeevo.self_sustainable.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin
{
    @Shadow
    @Final
    private Random random;
    @Shadow
    private int ticks;
    @Shadow
    @Final
    private static Identifier FOOD_EMPTY_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_HALF_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_FULL_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_EMPTY_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_HALF_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_FULL_TEXTURE;
    @Unique
    private int foodOverlayShakeCounter = 0;


    // For progressive crafting
    @Inject(method = "renderFood", at = @At("HEAD"))
    private void onRenderFood(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        drawFoodOverlay(context, player, top, right);
    }

    @Unique
    private void drawFoodOverlay(DrawContext context, PlayerEntity player, int top, int right) {
        HungerManager hungerManager = player.getHungerManager();
        int i = hungerManager.getFoodLevel();
        float saturationLevel = hungerManager.getSaturationLevel();
        RenderSystem.enableBlend();

        // Calculate how many full pips to render (each full pip equals 6 hunger points)
        int fullPips = i / 6; // Calculate how many full pips are needed
        int remainingHunger = i % 6; // Calculate remaining hunger for fractional pip (1/6th)

        // Loop through 10 hunger slots (slots are always 10 in Minecraft)
        for (int j = 0; j < 10; ++j) {
            Identifier identifier3;
            Identifier identifier2;
            Identifier identifier;
            int k = top;

            // Check for hunger status effects
            if (player.hasStatusEffect(StatusEffects.HUNGER)) {
                identifier = FOOD_EMPTY_HUNGER_TEXTURE;
                identifier2 = FOOD_HALF_HUNGER_TEXTURE;
                identifier3 = FOOD_FULL_HUNGER_TEXTURE;
            } else {
                identifier = FOOD_EMPTY_TEXTURE;
                identifier2 = FOOD_HALF_TEXTURE;
                identifier3 = FOOD_FULL_TEXTURE;
            }

            // Apply random offset if saturation is 0 (for animation effect)
            if (saturationLevel <= 0.0f && this.ticks % (i * 3 + 1) == 0) {
                k += this.random.nextInt(3) - 1;
            }

            // Calculate the position for each icon
            int l = right - j * 8 - 9;

            // Draw the empty texture
            context.drawGuiTexture(identifier, 0, 0, 0, 9, l, k, 9, 9);

            // Render full pips based on food level
            if (j < fullPips) {
                context.drawGuiTexture(identifier3, 0, 0, 0, 9, l, k, 9, 9); // Draw full pip
            }

            // Render half pip if needed
            if (j == fullPips && remainingHunger >= 3) {
                context.drawGuiTexture(identifier2, 0, 0, 0, 9, l, k, 9, 9); // Draw half pip
            }

            // Render fractional pip for remaining hunger if needed
            if (j == fullPips && remainingHunger > 0 && remainingHunger < 3) {
                int fractionWidth = (int)(9 * (remainingHunger / 6.0));  // Fractional width of pip
                context.drawGuiTexture(identifier3, 0, 0, 0, fractionWidth, l, k, fractionWidth, 9);  // Draw fractional pip
            }
        }
        RenderSystem.disableBlend();
    }
}

    /**
    @Unique
    private void drawFoodOverlay(DrawContext context, PlayerEntity player, int iScreenX, int iScreenY) {
        HungerManager hungerManager = player.getHungerManager();
        int iFoodLevel = hungerManager.getFoodLevel();
        float fSaturationLevel = hungerManager.getSaturationLevel();
        RenderSystem.enableBlend();

        // Convert saturation level to pips (multiplied by 4 as in the original mod)
        int iSaturationPips = (int)((fSaturationLevel + 0.124F) * 4F);

        // Determine the number of full food pips (1/6th of a pip per full pip)
        int iFullHungerPips = iFoodLevel / 6;

        /**
        // Handle shaking effect if exhaustion was added
        if (MinecraftClient.getInstance().player.exhaustionAddedSinceLastGuiUpdate) {
            foodOverlayShakeCounter = 20;
            MinecraftClient.getInstance().player.exhaustionAddedSinceLastGuiUpdate = false;
        } else if (foodOverlayShakeCounter > 0) {
            foodOverlayShakeCounter--;
        }


        // Loop through and render each food pip
        for (int iTempCount = 0; iTempCount < 10; ++iTempCount) {
            int iShankScreenY = iScreenY;
            int iShankTextureOffsetX = 16; // Default texture for empty hunger bar
            byte iBackgroundTextureOffsetX = 0;

            // Check if the player has the hunger effect
            if (MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.HUNGER)) {
                iShankTextureOffsetX += 36;
                iBackgroundTextureOffsetX = 13; // Hunger effect overlay
            }
            // Determine if the current pip should have a background
            else if (iTempCount < iSaturationPips / 8) {
                iBackgroundTextureOffsetX = 1; // Active saturation overlay
            }

            // Handle shaking effect for low saturation
            if (player.getStatusEffect(StatusEffects.HUNGER) != null && this.ticks % (iFoodLevel * 5 + 1) == 0) {
                iShankScreenY = iScreenY + (this.random.nextInt(3) - 1);
            } else if (foodOverlayShakeCounter > 0) {
                int iShakeAmount = 1;
                if (player.getRandom().nextInt(2) == 0) {
                    iShakeAmount = -iShakeAmount;
                }
                iShankScreenY = iScreenY + iShakeAmount;
            }

            int iShankScreenX = iScreenX - iTempCount * 8 - 9;

            // Draw the background texture
            context.drawGuiTexture(iShankScreenX, iShankScreenY, 16 + iBackgroundTextureOffsetX * 9, 27, 9, 9);

            // Check if this pip is the current one to be partially filled
            if (iTempCount == iSaturationPips / 8) {
                if (!MinecraftClient.getInstance().player.hasStatusEffect(StatusEffects.HUNGER)) {
                    int iPartialPips = iSaturationPips % 8;

                    if (iPartialPips != 0) {
                        // Draw partial saturation pips based on fractional value
                        context.drawGuiTexture(iShankScreenX + 8 - iPartialPips, iShankScreenY, 25 + 8 - iPartialPips, 27, 1 + iPartialPips, 9);
                    }
                }
            }

            // Render the full food pips
            if (iTempCount < iFullHungerPips) {
                context.drawGuiTexture(iShankScreenX, iShankScreenY, iShankTextureOffsetX + 36, 27, 9, 9);
            }
            // Render the partial food pip if necessary
            else if (iTempCount == iFullHungerPips) {
                int iPartialPips = iFoodLevel % 6;

                if (iPartialPips != 0) {
                    // Draw the partial pip for the food level
                    context.drawGuiTexture(iShankScreenX + 7 - iPartialPips, iShankScreenY, iShankTextureOffsetX + 36 + 7 - iPartialPips, 27, 3 + iPartialPips, 9);
                }
            }
        }
    }
    **/

