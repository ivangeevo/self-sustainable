package org.btwr.self_sustainable.event.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.btwr.self_sustainable.tag.ModTags;

public class ModBlockBreakEvents {

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(ModBlockBreakEvents::afterWoodenChestBroken);
    }

    // Play sound if a wooden chest is broken incorrectly
    private static void afterWoodenChestBroken(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity be) {
        if (world.isClient) return;

        if (!state.isIn(ConventionalBlockTags.WOODEN_CHESTS)) return;

        if (!isChestHarvestingAxe(player) && !player.getAbilities().creativeMode) {
            world.playSound(
                    null,
                    pos,
                    ModSoundEvents.WOODEN_CHEST_INCORRECT_BREAK,
                    SoundCategory.BLOCKS,
                    0.25F,
                    1.0F + (world.getRandom().nextFloat() * 0.25F)
            );
        }
    }

    private static boolean isChestHarvestingAxe(PlayerEntity player) {
        ItemStack stack = player.getMainHandStack();
        Item item = stack.getItem();

        boolean isStrongEnough = false;

        // Check if axe is strong enough to harvest a wooden chest normally. Doesn't apply for golden axes
        if (item instanceof ToolItem toolItem) {
            ToolMaterial toolMaterial = toolItem.getMaterial();
            boolean isAxe = stack.isIn(ItemTags.AXES);
            boolean aboveStoneSpeed = toolMaterial.getMiningSpeedMultiplier() > ToolMaterials.STONE.getMiningSpeedMultiplier();
            boolean isGoldToolMaterial = toolMaterial == ToolMaterials.GOLD;

            isStrongEnough = (isAxe && aboveStoneSpeed  && !isGoldToolMaterial) || stack.isIn(ModTags.Items.AXES_CAN_HARVEST_CHEST);
        }

        return isStrongEnough;
    }
}
