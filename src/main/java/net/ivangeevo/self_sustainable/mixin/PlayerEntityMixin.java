package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements  ItemAdded
{
    @Shadow public abstract boolean isPlayer();

    @Shadow public abstract void jump();

    @Shadow public abstract PlayerInventory getInventory();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }


    // Injected logic for periodic exhaustion
    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void injectedTickMovement(CallbackInfo ci)
    {
        PlayerEntity player = (PlayerEntity) (Object) this;

        if (!player.isCreative())
        {
            if (player.age % 1000 == 0)
            {
                // Modify the exhaustion using the HungerManager's exhaustion field
                HungerManager hungerManager = player.getHungerManager();
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

    //@Inject(method = "tick", at = @At("HEAD"))
    private void injectedTickTop(CallbackInfo ci) {

        ItemStack stack = this.getInventory().getMainHandStack();
        PlayerEntity player = (PlayerEntity)(Object)this;

        // FCMOD: Added
        //updateUsingItem(stack,world, player);
        // END FCMOD

    }




    /** ----Modify food exhaustion values for jumping and jump sprinting---- **/

    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.2f))
    private float modifySprintJump(float constant){
        return 1.00f;
    }

    @ModifyConstant(method = "jump", constant = @Constant(floatValue = 0.05f))
    private float modifyJump(float constant){
        return 0.40f;
    }

    /** --------------------------------------------------------------------- **/
}
