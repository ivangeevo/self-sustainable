package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.entity.interfaces.PlayerEntityAdded;
import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements  ItemAdded, PlayerEntityAdded
{
    @Shadow public abstract boolean isPlayer();
    @Shadow public abstract void jump();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
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
