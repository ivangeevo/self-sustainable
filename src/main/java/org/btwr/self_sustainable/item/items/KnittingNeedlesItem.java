package org.btwr.self_sustainable.item.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KnittingNeedlesItem extends ProgressiveCraftingItem {

    public KnittingNeedlesItem(Item.Settings group) {
        super(group);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        // stupid large so it's never actually hit in practice
        return 72000;
    }

    @Override
    public boolean btwr$getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        return true;
    }

    @Override
    public boolean btwr$getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return true;
    }

}