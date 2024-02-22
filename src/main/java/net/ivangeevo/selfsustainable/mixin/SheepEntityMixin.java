package net.ivangeevo.selfsustainable.mixin;

import com.google.common.collect.Maps;
import net.ivangeevo.selfsustainable.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements Shearable {
    @Shadow public abstract void setSheared(boolean sheared);

    @Shadow public abstract DyeColor getColor();

    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }




}
