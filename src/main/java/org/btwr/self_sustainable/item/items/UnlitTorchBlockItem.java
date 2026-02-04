package org.btwr.self_sustainable.item.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.self_sustainable.util.TorchIgnitionHelper;

public class UnlitTorchBlockItem extends VerticallyAttachableBlockItem {

    /**
     * @param standingBlock
     * @param wallBlock
     * @param settings
     * @param verticalAttachmentDirection the direction of the item's vertical attachment, {@link Direction#UP} for hanging blocks
     *                                    and {@link Direction#DOWN} for standing blocks
     */
    public UnlitTorchBlockItem(Block standingBlock, Block wallBlock, Settings settings, Direction verticalAttachmentDirection) {
        super(standingBlock, wallBlock, settings, verticalAttachmentDirection);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        ItemStack heldStack = context.getStack();

        // Try lighting from a lit block
        if (state.isIn(ModTags.Blocks.DIRECTLY_IGNITES_ITEM_ON_USE) || state.contains(Properties.LIT) && state.get(Properties.LIT)) {
            Item litTorch = heldStack.isOf(ModItems.SOUL_TORCH_UNLIT) ? Items.SOUL_TORCH : Items.TORCH;
            TorchIgnitionHelper.lightInfiniteTorch(litTorch, world, pos, player, context.getHand(), heldStack);
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

}
