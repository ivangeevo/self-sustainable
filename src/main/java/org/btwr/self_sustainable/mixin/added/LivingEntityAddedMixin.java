package org.btwr.self_sustainable.mixin.added;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.btwr.self_sustainable.entity.interfaces.LivingEntityAdded;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAddedMixin extends Entity implements LivingEntityAdded {

    @Shadow protected int itemUseTimeLeft;

    public LivingEntityAddedMixin(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    public void setItemUseTime(int iCount)
    {
        itemUseTimeLeft = iCount;
    }

}