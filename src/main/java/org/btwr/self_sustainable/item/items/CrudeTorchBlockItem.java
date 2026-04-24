package org.btwr.self_sustainable.item.items;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import org.btwr.self_sustainable.block.blocks.AbstractCrudeTorchBlock;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.TorchFuelComponent;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.minecraft.state.property.Properties.LIT;

public class CrudeTorchBlockItem extends VerticallyAttachableBlockItem implements FabricItem {

    TorchFireState torchState;
    ModTorchHandler handler;
    private static final int FUEL_TIME = 24000;
    int maxFuel = FUEL_TIME;
    public static final int SPUTTER_TIME = 30 * 20; // 30 seconds
    public CrudeTorchBlockItem(Block standingBlock, Block wallBlock, Item.Settings settings, TorchFireState torchState, ModTorchHandler group) {
        super(standingBlock, wallBlock, settings, Direction.DOWN);
        this.torchState = torchState;
        this.handler = group;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (state.isIn(ModTags.Blocks.DIRECTLY_IGNITES_ITEM_ON_USE) || (state.contains(LIT) && state.get(LIT))) {
            if (torchState != TorchFireState.UNLIT) return ActionResult.FAIL;
            if (!world.isClient) {
                PlayerEntity player = context.getPlayer();

                if (player != null) {
                    // torch you get out of lighting
                    ItemStack litTorch = stateStack(stack, TorchFireState.LIT);

                    // consume one unlit torch
                    stack.decrement(1);

                    if (stack.isEmpty()) {
                        // hand goes empty, replace it with the lit torch
                        player.setStackInHand(context.getHand(), litTorch);
                    }
                    else {
                        // leave the unlit stack in hand
                        player.setStackInHand(context.getHand(), stack);

                        // try to add the lit torch to inventory, or drop if full
                        if (!player.getInventory().insertStack(litTorch)) {
                            player.dropItem(litTorch, false);
                        }
                    }
                }

                world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
            }

            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        int fuel = getFuel(stack);

        return fuel > 0 && fuel < maxFuel;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int fuel = getFuel(stack);

        if (maxFuel != 0) {
            return Math.round(13.0f - (maxFuel - fuel) * 13.0f / maxFuel);
        }

        return 0;
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        boolean oldHasFuel = oldStack.getComponents().contains(ModComponentsTypes.TORCH_FUEL);
        boolean newHasFuel = newStack.getComponents().contains(ModComponentsTypes.TORCH_FUEL);

        return oldHasFuel != newHasFuel;
    }

    public static ItemStack stateStack(ItemStack inputStack, TorchFireState newState) {
        ItemStack outputStack = ItemStack.EMPTY;

        if (inputStack.getItem() instanceof BlockItem && inputStack.getItem() instanceof CrudeTorchBlockItem) {
            AbstractCrudeTorchBlock newBlock = (AbstractCrudeTorchBlock) ((BlockItem)inputStack.getItem()).getBlock();
            CrudeTorchBlockItem newItem = (CrudeTorchBlockItem) newBlock.handler.getStandingTorch(newState).asItem();

            outputStack = changedCopy(inputStack, newItem);
            if (newState == TorchFireState.BURNED_OUT) outputStack.remove(ModComponentsTypes.TORCH_FUEL);
        }

        return outputStack;
    }

    public static int getFuel(ItemStack stack) {
        return stack.getOrDefault(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent()).getFuel();
    }

    public TorchFireState getTorchState() {
        return torchState;
    }

    public static ItemStack changedCopy(ItemStack stack, Item replacementItem) {
        ItemStack itemStack = new ItemStack(replacementItem, 1);

        // copy TorchFuelComponent if present
        if (stack.getComponents().contains(ModComponentsTypes.TORCH_FUEL)) {
            TorchFuelComponent fuel = stack.getOrDefault(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent());
            itemStack.set(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent(fuel.getFuel()));
        }

        return itemStack;
    }

    public static ItemStack addFuel(ItemStack stack, World world, int amount) {
        if (!(stack.getItem() instanceof CrudeTorchBlockItem torchItem) || world.isClient) {
            return stack;
        }

        int fuel = getFuel(stack);
        fuel += amount;

        // burnout
        if (fuel <= 0) {
            return ItemStack.EMPTY;
        }

        // transition to smoldering
        if (fuel <= SPUTTER_TIME && torchItem.getTorchState() == TorchFireState.LIT) {
            stack = stateStack(stack, TorchFireState.SMOULDER);
        }

        // clamp + persist fuel
        if (fuel > FUEL_TIME) {
            fuel = FUEL_TIME;
        }

        stack.set(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent(fuel));
        return stack;
    }


    @Override
    public boolean btwr$getCanItemStartFireOnUse(ItemStack stack) {
        return torchState == TorchFireState.LIT || torchState == TorchFireState.SMOULDER;
    }

}