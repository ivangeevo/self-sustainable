package net.ivangeevo.self_sustainable.entity.interfaces;

import net.minecraft.item.ItemStack;

public interface PlayerEntityAdded
{

    ItemStack itemInUse();

    void setItemInUse(ItemStack stack, int par2);

}
