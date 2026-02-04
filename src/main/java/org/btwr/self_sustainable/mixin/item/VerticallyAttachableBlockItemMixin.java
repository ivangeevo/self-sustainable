package org.btwr.self_sustainable.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import org.btwr.self_sustainable.tag.ModTags;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VerticallyAttachableBlockItem.class)
public abstract class VerticallyAttachableBlockItemMixin extends BlockItem {

    public VerticallyAttachableBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean btwr$getCanItemStartFireOnUse(ItemStack stack) {
        return stack.isIn(ModTags.Items.LIT_TORCHES);
    }
}
