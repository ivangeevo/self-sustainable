package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.entity.interfaces.PlayerEntityAdded;
import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements  ItemAdded, PlayerEntityAdded
{
    @Shadow public abstract boolean isPlayer();
    @Shadow public abstract void jump();

    @Shadow @Final private PlayerAbilities abilities;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    public void modifyMovementSpeed(CallbackInfoReturnable<Float> cir)
    {
        PlayerEntity player = (PlayerEntity) (Object) this;
        int hungerLevel = player.getHungerManager().getFoodLevel();
        float healthLevel = player.getHealth();
        float speedMultiplier = 1.0f;

        // Logic for modifying speed based on hunger level
        if (hungerLevel <= 2)
        {
            speedMultiplier *= 0.25f;  // Starving
            // Additional effects for starving
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200, 1));
            player.damage(this.getDamageSources().starve(), 1.0f);
        }
        else if (hungerLevel <= 4)
        {
            speedMultiplier *= 0.5f;  // Famished
            // Additional effects for famished
            player.getAbilities().allowFlying = false;  // Loss of jump
        }
        else if (hungerLevel <= 6)
        {
            speedMultiplier *= 0.75f;  // Hungry
            // Additional effects for hungry
            // Assuming "Hand Crank" is some custom action, disabling it
            // player.disableHandCrank();  // Inability to use the Hand Crank
        }
        else if (hungerLevel <= 8)
        {
            speedMultiplier *= 1.0f;  // Peckish
            // Additional effects for peckish
            player.setSprinting(false);  // Loss of sprint
        }

        // Logic for modifying speed based on health level
        if (healthLevel <= 2) {
            speedMultiplier *= 0.25f;  // Dying
            // Additional effects for dying
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200, 1));
        } else if (healthLevel <= 4) {
            speedMultiplier *= 0.5f;  // Crippled
            /** Additional effects for crippled **/
            // Loss of jump
            // Inability to swim
        } else if (healthLevel <= 6) {
            speedMultiplier *= 0.75f;  // Wounded
        } else if (healthLevel <= 8) {
            speedMultiplier *= 1.0f;  // Injured
        } else if (healthLevel <= 10) {
            speedMultiplier *= 1.0f;  // Hurt
            // Additional effects for hurt
            player.setSprinting(false);  // Loss of sprint
        }

        cir.setReturnValue(cir.getReturnValue() * speedMultiplier);
    }


    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    public void onJump(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        int hungerLevel = player.getHungerManager().getFoodLevel();
        float healthLevel = player.getHealth();


        // Prevent jumping if hunger level or health is below 4 (2 shanks/hearts)
        if (hungerLevel < 4 || healthLevel <= 4)
        {
            ci.cancel();
        }


    }

    // Injected logic for periodic exhaustion
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void injectedTickMovement(CallbackInfo ci)
    {
        PlayerEntity player = (PlayerEntity) (Object) this;
        HungerManager hungerManager = player.getHungerManager();

        if (!player.isCreative())
        {
            if (player.age % 1000 == 0)
            {
                hungerManager.addExhaustion(1.25f);
            }
        }
    }

    // Additional logic for slowly healing the player every "x" ticks
    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;

        int healTicks = 600;
        if (player.age % healTicks == 0 && player.getHealth() < player.getMaxHealth()
                && player.getHungerManager().getFoodLevel() >= 9)
        {
            player.heal(1.0F);
        }
    }

    /** Modify food exhaustion values for jumping and jump sprinting **/

    // Sprint jumping
    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.2f))
    private float modifySprintJump(float constant){
        return 1.00f;
    }

    // Regular jumping
    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.05f))
    private float modifyJump(float constant){
        return 0.40f;
    }

    /** --------------------------------------------------------------------- **/
}
