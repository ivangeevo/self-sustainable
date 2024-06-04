package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VerticallyAttachableBlockItem.class)
public abstract class VerticallyAttachableBlockItemMixin extends BlockItem
{

    public VerticallyAttachableBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean getCanItemStartFireOnUse(ItemStack stack)
    {
        return stack.isIn(ModTags.Items.DIRECT_IGNITERS);
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(ItemStack stack)
    {
        return stack.isIn(ModTags.Items.DIRECT_IGNITERS);
    }



}
