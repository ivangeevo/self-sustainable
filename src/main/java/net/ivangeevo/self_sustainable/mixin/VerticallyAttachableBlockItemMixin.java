package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VerticallyAttachableBlockItem.class)
public class VerticallyAttachableBlockItemMixin extends BlockItem
{


    public VerticallyAttachableBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (world.canPlayerModifyAt(player, pos))
        {
            if (!world.isClient)
            {
                attemptToLightBlock(context.getStack(), world, pos, context.getSide());
            }

            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;

    }

    @Override
    public boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing)
    {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if ( isTorch(stack) && targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, pos) )
        {
            return targetBlock.setOnFireDirectly(world, pos);
        }

        return false;
    }


    private boolean isTorch(ItemStack stack)
    {
        return stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH);
    }


}
