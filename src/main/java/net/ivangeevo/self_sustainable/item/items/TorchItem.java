package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.Block;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.math.Direction;

/** A regular torch item. Gets put out only if submerged in water. **/
public class TorchItem extends VerticallyAttachableBlockItem implements Ignitable
{
    public TorchItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection)
    {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    /**
    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        BlockPos pos = context.getBlockPos();
        BlockState state = context.getWorld().getBlockState(pos);

        boolean isStandingBlock = state.isOf(ModBlocks.TORCH.getStateWithProperties(state.with(LIT, false)).getBlock());
        boolean isWallBlock = wallBlock.getStateManager().getDefaultState().isOf(state.with(LIT, false).getBlock());

        if ( isStandingBlock )
        {
            context.getWorld().setBlockState(pos, ModBlocks.TORCH.getStateWithProperties(state.with(LIT,true)), 3);
            playLitFX(context.getWorld(), pos);
            return ActionResult.SUCCESS;
        }
        else if (isWallBlock)
        {
            context.getWorld().setBlockState(pos, ModBlocks.WALL_TORCH.getStateWithProperties(state.with(LIT,true)), 3);
            playLitFX(context.getWorld(), pos);
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }
    **/






}
