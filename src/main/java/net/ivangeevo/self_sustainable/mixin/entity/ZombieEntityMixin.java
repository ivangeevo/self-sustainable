package net.ivangeevo.self_sustainable.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    // Make Zombie Villagers not burn in daylight
    @Inject(method = "burnsInDaylight", at = @At("HEAD"), cancellable = true)
    private void stopDaylightBurns(CallbackInfoReturnable<Boolean> cir) {
        if (this.isZombieVillager()) {
            cir.setReturnValue(false);
        }
    }

    // Make Zombie Villagers run faster when attacking
    @Inject(method = "initCustomGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/goal/GoalSelector;add(ILnet/minecraft/entity/ai/goal/Goal;)V", ordinal = 0))
    private void modifyZombieAttackGoal(CallbackInfo ci) {
        if (!this.isZombieVillager()) {
            return;
        }

        // Set a faster attack speed
        ZombieAttackGoal fasterAttackGoal =
                new ZombieAttackGoal((ZombieEntity)(Object)this, 1.5, false);

        this.goalSelector.add(2, fasterAttackGoal);
    }

    @Unique
    private boolean isZombieVillager() {
        return (ZombieEntity)(Object)this instanceof ZombieVillagerEntity;
    }

}
