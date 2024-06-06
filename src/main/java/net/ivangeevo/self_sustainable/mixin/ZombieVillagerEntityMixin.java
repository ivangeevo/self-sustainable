package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillagerEntity.class)
public abstract class ZombieVillagerEntityMixin extends ZombieEntity
{
    public ZombieVillagerEntityMixin(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    // Make Zombie villagers not despawn.
    @Inject(method = "canImmediatelyDespawn", at = @At("HEAD"), cancellable = true)
    private void injectedCanImmediatelyDespawn(double distanceSquared, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(false);
    }

    @Override protected boolean burnsInDaylight()
    {
        return false;
    }

}
