package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.Difficulty;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin
{
    @Shadow
    private float saturationLevel;
    @Shadow private int prevFoodLevel = 20;
    @Shadow private float exhaustion;
    @Shadow private int foodLevel = 20;
    @Shadow private int foodTickTimer;



    @ModifyConstant(method = "<init>",
            constant = @Constant(floatValue = 5.0f),
            slice = @Slice(
            from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                    target = "net/minecraft/entity/player/HungerManager.foodLevel : I"),
            to = @At(value = "FIELD", opcode = Opcodes.PUTFIELD,
                    target = "net/minecraft/entity/player/HungerManager.saturationLevel : F"))
    )
    private float modifySaturationLevel(float original) {
        return 0.001f;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void update(PlayerEntity player) {
        Difficulty difficulty = player.getWorld().getDifficulty();
        this.prevFoodLevel = this.foodLevel;
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.foodLevel = Math.max(this.foodLevel - 1, 0);
            }
        }

        /** Removed the logic for saturation based healing, & difficulty based healing **/


        // Custom logic for starvation when food level is 0
        if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (player.getHealth() > 10.0F || difficulty == Difficulty.HARD || player.getHealth() > 1.0F && difficulty == Difficulty.NORMAL) {
                    player.setStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA,40,4,true,true), player);
                    player.damage(player.getDamageSources().starve(), 1.0F);

                }
                this.foodTickTimer = 0;
            }
        }
    }
}
