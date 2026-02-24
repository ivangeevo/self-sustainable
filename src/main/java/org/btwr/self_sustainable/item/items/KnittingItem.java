package org.btwr.self_sustainable.item.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.util.DyeColorUtils;
import org.btwr.self_sustainable.util.ItemUtils;
import org.btwr.shared_library.api.item.ProgressiveCraftingItem;

public class KnittingItem extends ProgressiveCraftingItem {

    public KnittingItem(Settings settings) {
        super(settings
                .maxDamage(ProgressiveCraftingItem.DEFAULT_MAX_DAMAGE)
        );
    }

    @Override
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player) {
        player.playSound(
                SoundEvents.BLOCK_WOOD_STEP,
                0.25F + 0.25F * (float) world.random.nextInt(2),
                (world.random.nextFloat() - world.random.nextFloat()) * 0.25F + 1.75F
        );
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        DyeColor color = getColor(stack);

        ItemStack woolStack = new ItemStack(ModItems.WOOL_KNITS.get(color), 1);
        //woolStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color.getEntityColor(), false));
        woolStack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color.getEntityColor() & 0x00FFFFFF, false));
        PlayerEntity player = (PlayerEntity)user;
        world.playSound(
                player,
                player.getBlockPos(),
                SoundEvents.BLOCK_WOOL_STEP,
                SoundCategory.BLOCKS,
                1F,
                world.getRandom().nextFloat() * 0.1F + 0.9F
        );
        ItemUtils.givePlayerStackOrEject(player, woolStack);

        return new ItemStack(ModItems.KNITTING_NEEDLES);
    }

    public static void setColor(ItemStack stack, DyeColor color) {
        DyedColorComponent component = new DyedColorComponent(color.getEntityColor() & 0x00FFFFFF, false);
        stack.set(DataComponentTypes.DYED_COLOR, component);
    }

    public static DyeColor getColor(ItemStack stack) {
        DyedColorComponent dyedColor = stack.get(DataComponentTypes.DYED_COLOR);
        if (dyedColor == null) return DyeColor.WHITE;
        return DyeColorUtils.getClosestDyeColor(dyedColor.rgb());
    }

}