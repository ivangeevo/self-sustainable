package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.entity.mob.MobEntity.getEquipmentForSlot;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity
{

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);



    @Inject(method = "initEquipment", at = @At(value = "HEAD"), cancellable = true)
    private void modifiedInitEqupment(Random random, LocalDifficulty localDifficulty, CallbackInfo ci)
    {
        if (random.nextFloat() < 0.15F * localDifficulty.getClampedLocalDifficulty()) {
            int i = random.nextInt(2);
            float f = this.getWorld().getDifficulty() == Difficulty.HARD ? 0.1F : 0.25F;
            if (random.nextFloat() < 0.011F) {
                ++i;
            }

            if (random.nextFloat() < 0.019F) {
                ++i;
            }

            if (random.nextFloat() < 0.027F) {
                ++i;
            }

            boolean bl = true;
            EquipmentSlot[] var6 = EquipmentSlot.values();
            int var7 = var6.length;

            for (EquipmentSlot equipmentSlot : var6) {
                if (equipmentSlot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack itemStack = this.getEquippedStack(equipmentSlot);
                    if (!bl && random.nextFloat() < f) {
                        break;
                    }

                    bl = false;
                    if (itemStack.isEmpty()) {
                        Item item = getEquipmentForSlot(equipmentSlot, i);
                        if (item != null) {
                            this.equipStack(equipmentSlot, new ItemStack(item));
                        }
                    }
                }
            }
        }

    }

    @Inject(method = "getEquipmentForSlot", at = @At(value = "HEAD"), cancellable = true)
    private static void customGetEquipmentForSlot(EquipmentSlot equipmentSlot, int equipmentLevel, CallbackInfoReturnable<Item> cir)
    {
        switch (equipmentSlot) {
            case HEAD:
                if (equipmentLevel == 0) {
                    cir.setReturnValue( Items.LEATHER_HELMET);
                } else if (equipmentLevel == 1) {
                    cir.setReturnValue( Items.GOLDEN_HELMET);
                }
            case CHEST:
                cir.setReturnValue( null);

            case LEGS:
                if (equipmentLevel == 0) {
                    cir.setReturnValue( Items.LEATHER_LEGGINGS);
                }
            case FEET:
                if (equipmentLevel == 0) {
                    cir.setReturnValue( Items.LEATHER_BOOTS);
                }
            default:
                cir.setReturnValue( null);
        }

    }
}
