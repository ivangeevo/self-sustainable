package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.slot.FurnaceFuelSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FurnaceFuelSlot.class)
public class FurnaceFuelSlotMixin {

    @Shadow @Final private AbstractFurnaceScreenHandler handler;

    @Inject(method = "getMaxItemCount", at = @At("HEAD"), cancellable = true)
    private void setMaxCount(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }

    //@Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    private void onCanInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.handler.propertyDelegate.get(1) == 0 && isFuel(stack) || FurnaceFuelSlot.isBucket(stack));
    }

    @Unique
    protected boolean isFuel(ItemStack itemStack) {
        return AbstractFurnaceBlockEntity.canUseAsFuel(itemStack);
    }

}
