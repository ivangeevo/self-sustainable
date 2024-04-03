package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

public interface IgnitionProvider {
    boolean canIgniteItem(ItemStack stack, BlockState state);
}
