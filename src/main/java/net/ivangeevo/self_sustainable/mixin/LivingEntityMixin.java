package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.entity.interfaces.LivingEntityAdded;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAdded
{
    @Shadow protected int itemUseTimeLeft;

    public LivingEntityMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    public void setItemUseTime(int iCount)
    {
        itemUseTimeLeft = iCount;
    }
}
