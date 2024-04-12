package net.ivangeevo.self_sustainable.item.items;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;


public class CrudeTorchItem extends TorchItem
{
    public CrudeTorchItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection) {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }
}
