package net.ivangeevo.self_sustainable.mixin.item;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.item.items.CrudeTorchBlockItem;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;
import static net.ivangeevo.self_sustainable.state.property.ModProperties.FUEL_LEVEL;
import static net.minecraft.state.property.Properties.LIT;

@Mixin(VerticallyAttachableBlockItem.class)
public abstract class VerticallyAttachableBlockItemMixin extends BlockItem {

    public VerticallyAttachableBlockItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack heldStack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState targetState = world.getBlockState(pos);

        // Try igniting torch when used on block that's lit up
        if (targetState.isIn(ModTags.Blocks.DIRECTLY_IGNITES_ITEM_ON_USE) || isSpecialLitUpBlock(targetState)) {
            if (targetState.isOf(ModBlocks.TORCH_UNLIT)) return ActionResult.FAIL;
            if (heldStack.getItem() instanceof VerticallyAttachableBlockItem && !(heldStack.getItem() instanceof CrudeTorchBlockItem)) {
                PlayerEntity player = context.getPlayer();
                if (player != null && !world.isClient && !heldStack.isOf(Items.SOUL_TORCH)) {
                    ItemStack newTorch = Items.TORCH.getDefaultStack().copyWithCount(heldStack.getCount());

                    if (player.getMainHandStack() == heldStack) {
                        player.getInventory().setStack(player.getInventory().selectedSlot, newTorch);
                    }
                    else if (player.getOffHandStack() == heldStack) {
                        player.getInventory().offHand.set(0, newTorch);
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                return ActionResult.SUCCESS;
            }
        }

        // Prevent normal placement on those blocks
        if (!(targetState.getBlock() instanceof CampfireBlock) || !targetState.isOf(ModBlocks.OVEN_BRICK) || !targetState.isOf(ModBlocks.SMOKER_BRICK))
        {
            return super.useOnBlock(context);
        }

        return ActionResult.FAIL;
    }

    @Unique
    private boolean isSpecialLitUpBlock(BlockState state) {
        boolean lit = state.contains(LIT) && state.get(LIT);
        boolean hasFireLevel = state.contains(FIRE_LEVEL) && state.get(FIRE_LEVEL) > 0;
        return (lit && (state.isOf(ModBlocks.OVEN_BRICK) || state.isOf(ModBlocks.SMOKER_BRICK)))
                || (hasFireLevel && state.getBlock() instanceof CampfireBlock);
    }

}