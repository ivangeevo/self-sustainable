package org.btwr.self_sustainable.item.interfaces.added;

import org.btwr.self_sustainable.util.CustomUseAction;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ItemAdded {

    int defaultFurnaceBurnTime = 0;

    default int btwr$getOvenBurnTime(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$getCanItemBeSetOnFireOnUse(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$getCanItemStartFireOnUse(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default int btwr$getCampfireBurnTime(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$canHarvestBlock(ItemStack stack, World world, BlockState state) {
        throw new UnsupportedOperationException();
    }
    default float btwr$getStrVsBlock(ItemStack stack, World world, BlockState state) {
        throw new UnsupportedOperationException();
    }

    default boolean btwr$isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        throw new UnsupportedOperationException();
    }

    default int btwr$getHerbivoreFoodValue(int iItemDamage) {
        throw new UnsupportedOperationException();
    }

    default Item btwr$setHerbivoreFoodValue(int iFoodValue) {
        throw new UnsupportedOperationException();
    }

    default Item btwr$setAsBasicHerbivoreFood() {
        throw new UnsupportedOperationException();
    }

    default int btwr$getItemUseWarmupDuration() {
        throw new UnsupportedOperationException();
    }

    default CustomUseAction btwr$getCustomUseAction(ItemStack stack) {
        throw new UnsupportedOperationException();
    }

}