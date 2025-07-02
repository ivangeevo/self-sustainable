package net.ivangeevo.self_sustainable.mixin.entity;

import net.fabricmc.loader.api.FabricLoader;
import net.ivangeevo.self_sustainable.entity.interfaces.PlayerEntityAdded;
import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements  ItemAdded, PlayerEntityAdded
{
    @Shadow public abstract boolean isPlayer();
    @Shadow public abstract void jump();

    @Shadow public abstract void incrementStat(Identifier stat);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Unique private final boolean isLoadedModImMovens = FabricLoader.getInstance().isModLoaded("im_movens");

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

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void onJump(CallbackInfo ci)
    {
        if (!isLoadedModImMovens)
        {
            super.jump();
            this.incrementStat(Stats.JUMP);
            if (this.isSprinting()) {
                this.addExhaustion(0.33F);
            } else {
                this.addExhaustion(0.12F);
            }

            ci.cancel();
        }

    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void injectedTick(CallbackInfo ci)
    {
        if (!isLoadedModImMovens)
        {
            PlayerEntity player = (PlayerEntity) (Object) this;

            // TODO: Fix? Maybe only serverPlayerEntity should heal?
            int healTicks = 200;
            if (player.age % healTicks == 0 && player.getHealth() < player.getMaxHealth()
                    && player.getHungerManager().getFoodLevel() >= 9)
            {
                player.heal(1.0F);

                // Maybe add a minimal exhaustion on heal when the im-movens mod is not present
                //  I was also tinkering with adding a satisfying sound on heal? dunno
                //player.addExhaustion(0.2f);

                // Play Brewing Stand Bubble sound
                //playSound(SoundEvents.BLOCK_BREWING_STAND_BREW, 0.5f, 0.6f);

                // Play Enchanting Table Use sound
                //playSound(SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, 0.3f, 0.8f);
            }
        }
    }



    /** --------------------------------------------------------------------- **/
}
