package ivangeevo.selfsustainable.mixin;

import com.google.common.collect.Maps;
import ivangeevo.selfsustainable.ModItems;
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

    @Inject(method = "sheared", at = @At("HEAD"), cancellable = true)

    private void onSheared(SoundCategory shearedSoundCategory, CallbackInfo ci) {
        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0f, 1.0f);
        this.setSheared(true);
        int i = 1;
        for (int j = 0; j < i; ++j) {
            ItemEntity itemEntity = this.dropItem(CUSTOM_DROPS.get(this.getColor()).getItem(), 1);
            if (itemEntity == null) continue;
            itemEntity.setVelocity(itemEntity.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
        }

        ci.cancel();

    }


    private static final Map<DyeColor, ItemStack> CUSTOM_DROPS = Util.make(Maps.newEnumMap(DyeColor.class), map -> {
        map.put(DyeColor.WHITE, ModItems.WHITE_WOOL.getDefaultStack());
        map.put(DyeColor.ORANGE, ModItems.ORANGE_WOOL.getDefaultStack());
        map.put(DyeColor.MAGENTA, ModItems.MAGENTA_WOOL.getDefaultStack());
        map.put(DyeColor.LIGHT_BLUE, ModItems.LIGHT_BLUE_WOOL.getDefaultStack());
        map.put(DyeColor.YELLOW, ModItems.YELLOW_WOOL.getDefaultStack());
        map.put(DyeColor.LIME, ModItems.LIME_WOOL.getDefaultStack());
        map.put(DyeColor.PINK, ModItems.PINK_WOOL.getDefaultStack());
        map.put(DyeColor.GRAY, ModItems.GRAY_WOOL.getDefaultStack());
        map.put(DyeColor.LIGHT_GRAY, ModItems.LIGHT_GRAY_WOOL.getDefaultStack());
        map.put(DyeColor.CYAN, ModItems.CYAN_WOOL.getDefaultStack());
        map.put(DyeColor.PURPLE, ModItems.PURPLE_WOOL.getDefaultStack());
        map.put(DyeColor.BLUE, ModItems.BLUE_WOOL.getDefaultStack());
        map.put(DyeColor.BROWN, ModItems.BROWN_WOOL.getDefaultStack());
        map.put(DyeColor.GREEN, ModItems.GREEN_WOOL.getDefaultStack());
        map.put(DyeColor.RED, ModItems.RED_WOOL.getDefaultStack());
        map.put(DyeColor.BLACK, ModItems.BLACK_WOOL.getDefaultStack());

    });


}
