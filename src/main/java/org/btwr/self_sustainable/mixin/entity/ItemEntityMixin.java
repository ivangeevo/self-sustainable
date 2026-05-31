package org.btwr.self_sustainable.mixin.entity;

import org.btwr.self_sustainable.entity.handler.TorchItemEntityHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {

    @Shadow public abstract ItemStack getStack();

    @Unique
    private boolean btwr$wasInFluid = false;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getWorld()Lnet/minecraft/world/World;", ordinal = 5))
    private void onTick(CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        TorchItemEntityHandler.tickTorchFuel(self);
        btwr$wasInFluid = TorchItemEntityHandler.tickTorchInWater(self, btwr$wasInFluid);
    }


}