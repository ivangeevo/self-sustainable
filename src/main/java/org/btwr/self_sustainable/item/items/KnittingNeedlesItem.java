package org.btwr.self_sustainable.item.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KnittingNeedlesItem extends Item {

    public KnittingNeedlesItem(Settings settings) {
        super(settings);
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