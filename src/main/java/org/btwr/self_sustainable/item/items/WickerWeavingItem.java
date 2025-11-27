package org.btwr.self_sustainable.item.items;

import org.btwr.self_sustainable.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WickerWeavingItem extends ProgressiveCraftingItem {

    // Takes half as long as other progressive crafting
    public static final int WICKER_WEAVING_MAX_DAMAGE = (60 * 20 / PROGRESS_TIME_INTERVAL);

    public WickerWeavingItem(Settings settings ) {
        super(settings);
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player) {
        float volume = 0.25F + 0.25F * (float)world.random.nextInt(2);
        float pitch = (world.random.nextFloat() - world.random.nextFloat()) * 0.25F + 1.75F;
        player.playSound(SoundEvents.BLOCK_GRASS_STEP, volume, pitch);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        float pitch = world.random.nextFloat() * 0.1F + 0.9F;
        world.playSoundFromEntity(null, user, SoundEvents.BLOCK_GRASS_STEP, SoundCategory.PLAYERS, 1.0F, pitch);
        return new ItemStack(ModItems.WICKER);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        // Hue=120 degrees, full saturation and brightness (full green)
        return MathHelper.hsvToRgb(1.0F / 3.0F, 1.0F, 1.0F);
    }

}