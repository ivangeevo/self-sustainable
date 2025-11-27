package org.btwr.self_sustainable.block.entity.util;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

public interface FuelBurningBlock {

    default boolean tryUse(TagKey torches, ItemStack stack) {
        return stack.isIn(torches);
    }

}