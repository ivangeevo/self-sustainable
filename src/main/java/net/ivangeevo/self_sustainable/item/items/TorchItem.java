package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.block.interfaces.TorchBlockAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

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
        if (wallBlock.getStateManager().getDefaultState().isOf(this.getBlock()) && !state.get(TorchBlockAdded.LIT))
        {
            context.getWorld().setBlockState(context.getBlockPos(),state.with(TorchBlockAdded.LIT, true), 3);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    public int getOvenBurnTime(int ticks) {
        return 0;
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(int fuelTicks) {
        return false;
    }

    @Override
    public boolean getCanItemStartFireOnUse(int fuelTicks) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int fuelTicks) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int fuelTicks) {
        return false;
    }

    @Override
    public int getCampfireBurnTime(int fuelTicks) {
        return 0;
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }

    @Override
    public int getHerbivoreFoodValue(int iItemDamage) {
        return 0;
    }

    @Override
    public Item setHerbivoreFoodValue(int iFoodValue) {
        return null;
    }

    @Override
    public Item setAsBasicHerbivoreFood() {
        return null;
    }

    @Override
    public void updateUsingItem(ItemStack stack, World world, PlayerEntity player) {

    }

    @Override
    public int getItemUseWarmupDuration() {
        return 0;
    }




}
