package net.ivangeevo.self_sustainable.mixin.item;

import net.ivangeevo.self_sustainable.block.blocks.AbstractModTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.item.items.CrudeTorchItem;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VerticallyAttachableBlockItem.class)
public abstract class VerticallyAttachableBlockItemMixin extends BlockItem
{

    public VerticallyAttachableBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // Make sure it's a torch and get its type
        if (stack.getItem() instanceof VerticallyAttachableBlockItem) {
            if (state.isIn(ModTags.Blocks.DIRECTLY_IGNITABLE_FROM_ON_USE)) {
                    // No lighting on unlit fires etc.
                    if (state.contains(Properties.LIT))
                        if (!state.get(Properties.LIT))
                            return super.useOnBlock(context);

                    PlayerEntity player = context.getPlayer();
                    if (player != null && !world.isClient)
                        player.getInventory().setStack(player.getInventory().selectedSlot, Items.TORCH.getDefaultStack().copyWithCount(stack.getCount()));
                    if (!world.isClient) world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                    return ActionResult.SUCCESS;
                }

        }

        return super.useOnBlock(context);
    }

}
