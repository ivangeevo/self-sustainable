package net.ivangeevo.self_sustainable.item.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemAdded {

    int defaultFurnaceBurnTime = 0;

    default int getOvenBurnTime(int ticks) {
        return 0;
    }


    default boolean getCanItemBeSetOnFireOnUse(int fuelTicks) {
        return false;
    }

    default boolean getCanItemStartFireOnUse(int fuelTicks) {
        return false;
    }

    default boolean getCanBeFedDirectlyIntoBrickOven(int fuelTicks) {
        return false;
    }

    default boolean getCanBeFedDirectlyIntoCampfire(int fuelTicks) {
        return false;
    }

    default int getCampfireBurnTime(int fuelTicks) {
        return 0;
    }

    default boolean canHarvestBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }
    default float getStrVsBlock(ItemStack stack, World world, BlockState state) {
        return 1F;
    }

    default boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }

    default int getHerbivoreFoodValue(int iItemDamage) {
        return 0;
    }

    default Item setHerbivoreFoodValue(int iFoodValue) {
        return null;
    }

    default Item setAsBasicHerbivoreFood() {
        return null;
    }


    default void updateUsingItem(ItemStack stack, World world, PlayerEntity player) {

    }

    default int getItemUseWarmupDuration() {
        return 0;
    }


}
