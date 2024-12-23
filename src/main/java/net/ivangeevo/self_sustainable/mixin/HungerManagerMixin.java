package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HungerManager.class)
public abstract class HungerManagerMixin
{
    @Shadow private float saturationLevel;
    @Shadow private float exhaustion;
    @Shadow private int foodTickTimer;

    // Custom added foodLevel
    @Unique private int modFoodLevel = 60;
    @Unique private int prevModFoodLevel = 60;


    @ModifyConstant(method = "<init>", constant = @Constant(floatValue = 5.0f),
            slice = @Slice(
                    from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "net/minecraft/entity/player/HungerManager.foodLevel : I"),
                    to = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "net/minecraft/entity/player/HungerManager.saturationLevel : F")
            )
    )
    private float modifySaturationLevel(float original) {
        return 0f;
    }


    @Inject(method = "addInternal", at = @At("HEAD"), cancellable = true)
    private void onAddInternal(int nutrition, float saturation, CallbackInfo ci) {
        this.modFoodLevel = MathHelper.clamp(nutrition + this.modFoodLevel, 0, 60);
        this.saturationLevel = MathHelper.clamp(saturation + this.saturationLevel, 0.0f, (float)this.modFoodLevel);

        ci.cancel();
    }


    // TODO: Check on ways to modify this less intrusively
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void update(PlayerEntity player) {
        Difficulty difficulty = player.getWorld().getDifficulty();
        this.prevModFoodLevel = this.modFoodLevel;
        if (this.exhaustion > 4.0F) {
            this.exhaustion -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (difficulty != Difficulty.PEACEFUL) {
                this.modFoodLevel = Math.max(this.modFoodLevel - 1, 0);
            }
        }

        /** Removed the logic for saturation based healing, & difficulty based healing **/

        // Custom logic for starvation when food level is 0
        if (this.modFoodLevel <= 0) {
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

    @Inject(method = "readNbt", at = @At("HEAD"), cancellable = true)
    private void onReadNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("foodLevel", NbtElement.NUMBER_TYPE)) {
            this.modFoodLevel = nbt.getInt("modFoodLevel");
            this.foodTickTimer = nbt.getInt("foodTickTimer");
            this.saturationLevel = nbt.getFloat("foodSaturationLevel");
            this.exhaustion = nbt.getFloat("foodExhaustionLevel");
        }

        ci.cancel();
    }

    @Inject(method = "writeNbt", at = @At("HEAD"), cancellable = true)
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putInt("modFoodLevel", this.modFoodLevel);
        nbt.putInt("foodTickTimer", this.foodTickTimer);
        nbt.putFloat("foodSaturationLevel", this.saturationLevel);
        nbt.putFloat("foodExhaustionLevel", this.exhaustion);

        ci.cancel();
    }

    @Inject(method = "getFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onGetFoodLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(modFoodLevel);
    }

    @Inject(method = "getPrevFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onGetPrevFoodLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(prevModFoodLevel);
    }

    @Inject(method = "setFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onSetFoodLevel(int foodLevel, CallbackInfo ci) {
        modFoodLevel = foodLevel;

        ci.cancel();
    }
}
