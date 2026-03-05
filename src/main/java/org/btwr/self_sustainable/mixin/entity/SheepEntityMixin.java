package org.btwr.self_sustainable.mixin.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin {

    @Shadow public abstract DyeColor getColor();

    // Lazy init the new drops map, so it's loaded after the WOOLS map from the mod is loaded
    @Unique
    private static Map<DyeColor, ItemConvertible> drops = null;

    @Unique
    private static Map<DyeColor, ItemConvertible> getDrops() {
        if (drops == null) {
            drops = Maps.newEnumMap(DyeColor.class);
            drops.putAll(ModItems.WOOLS);
        }
        return drops;
    }

    @ModifyArg(method = "sheared", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;dropItem(Lnet/minecraft/item/ItemConvertible;I)Lnet/minecraft/entity/ItemEntity;"))
    private ItemConvertible onShear(ItemConvertible par1) {
        return getDrops().get(this.getColor());
    }
}
