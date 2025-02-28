package net.ivangeevo.self_sustainable.mixin.item;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.items.CrudeTorchItem;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;

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
        if (stack.getItem() instanceof VerticallyAttachableBlockItem && !(stack.getItem() instanceof CrudeTorchItem)) {
            if (state.isIn(ModTags.Blocks.DIRECTLY_IGNITABLE_FROM_ON_USE)) {

                if (state.getBlock() instanceof CampfireBlock) {
                    if (!(state.get(FIRE_LEVEL) > 0)) {
                        return ActionResult.PASS;
                    }

                }

                /**
                // No lighting on unlit fires etc.
                    if (state.contains(Properties.LIT))
                        if (!state.get(Properties.LIT) || !isLitTorchBlock(state))
                            return super.useOnBlock(context);
                 **/

                PlayerEntity player = context.getPlayer();
                    if (player != null && !world.isClient)
                        player.getInventory().setStack(player.getInventory().selectedSlot, Items.TORCH.getDefaultStack().copyWithCount(stack.getCount()));
                    if (!world.isClient) world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                    return ActionResult.SUCCESS;
                }

        }

        return super.useOnBlock(context);
    }
    private boolean isLitTorchBlock(BlockState state) {
        return state.isOf(ModBlocks.CRUDE_TORCH_LIT)
                || state.isOf(ModBlocks.CRUDE_TORCH_SMOULDER)
                || state.isOf(ModBlocks.CRUDE_WALL_TORCH_LIT)
                || state.isOf(ModBlocks.CRUDE_WALL_TORCH_SMOULDER)
                || state.isOf(Blocks.TORCH)
                || state.isOf(Blocks.SOUL_TORCH)
                || state.isOf(Blocks.WALL_TORCH)
                || state.isOf(Blocks.SOUL_WALL_TORCH)



                ;
    }

}
