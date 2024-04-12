package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.block.blocks.ModTorchBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;

import java.util.Objects;

/** A regular torch item. Gets put out only if fully submerged in water. **/
public class TorchItem extends VerticallyAttachableBlockItem
{
    public TorchItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection)
    {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState state = Objects.requireNonNull(context.getPlayer()).getBlockStateAtPos();
        if (!state.get(Properties.LIT))
        {
            context.getWorld().setBlockState(context.getBlockPos(),state.with(ModTorchBlock.LIT, true), 3);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }
}
