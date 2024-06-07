package net.ivangeevo.self_sustainable.item.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.math.Direction;

/** A crude torch gets put out after a certain time.In addition to submerging, also gets put out in rain. **/
public class CrudeTorchItem extends VerticallyAttachableBlockItem
{
    public CrudeTorchItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection) {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(ItemStack stack)
    {

        return true;
    }
}
