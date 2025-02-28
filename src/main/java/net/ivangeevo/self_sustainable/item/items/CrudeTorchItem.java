package net.ivangeevo.self_sustainable.item.items;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.ivangeevo.self_sustainable.block.blocks.AbstractModTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.item.component.TorchFuelComponent;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;

public class CrudeTorchItem extends VerticallyAttachableBlockItem implements FabricItem {
    TorchFireState torchState;
    ModTorchHandler handler;
    private static final int FUEL_TIME = 24000;
    int maxFuel = FUEL_TIME;

    public CrudeTorchItem(Block standingBlock, Block wallBlock, Item.Settings settings, TorchFireState torchState, ModTorchHandler group) {
        super(standingBlock, wallBlock, settings, Direction.DOWN);
        this.torchState = torchState;
        this.handler = group;
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
        boolean oldHasFuel = oldStack.getComponents().contains(ModComponents.TORCH_FUEL_COMPONENT);
        boolean newHasFuel = newStack.getComponents().contains(ModComponents.TORCH_FUEL_COMPONENT);

        return oldHasFuel != newHasFuel;
    }


    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        // Make sure it's a torch and get its type
        if (stack.getItem() instanceof CrudeTorchItem) {
            TorchFireState torchState = ((CrudeTorchItem) stack.getItem()).torchState;

            if (torchState == TorchFireState.UNLIT || torchState == TorchFireState.SMOULDER) {
                // Unlit and Smoldering
                if (state.isIn(ModTags.Blocks.DIRECTLY_IGNITABLE_FROM_ON_USE)) {

                    if (state.getBlock() instanceof CampfireBlock) {
                        if (!(state.get(FIRE_LEVEL) > 0)) {
                            return ActionResult.PASS;
                        }

                    }

                    /**
                    // No lighting on unlit fires etc.
                    if (state.contains(Properties.LIT))
                        if (!state.get(Properties.LIT))
                            return super.useOnBlock(context);
                     **/

                    PlayerEntity player = context.getPlayer();
                    if (player != null && !world.isClient)
                        if (stack.getCount() == 1) {
                            player.setStackInHand(context.getHand(), stateStack(stack, TorchFireState.LIT));
                        } else {
                            player.giveItemStack(stateStack(stack, TorchFireState.LIT));
                        }
                    //Ignitable.playLitFX(world, pos);
                    if (!world.isClient) world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.5f, 1.2f);
                    return ActionResult.SUCCESS;
                }
            }
        }

        return super.useOnBlock(context);
    }

    public static ItemStack stateStack(ItemStack inputStack, TorchFireState newState) {
        ItemStack outputStack = ItemStack.EMPTY;

        if (inputStack.getItem() instanceof BlockItem && inputStack.getItem() instanceof CrudeTorchItem) {
            AbstractModTorchBlock newBlock = (AbstractModTorchBlock) ((BlockItem)inputStack.getItem()).getBlock();
            CrudeTorchItem newItem = (CrudeTorchItem) newBlock.handler.getStandingTorch(newState).asItem();

            outputStack = changedCopy(inputStack, newItem);
            if (newState == TorchFireState.BURNED_OUT) outputStack.remove(ModComponents.TORCH_FUEL_COMPONENT);
        }

        return outputStack;
    }

    public static int getFuel(ItemStack stack) {
        return stack.getOrDefault(ModComponents.TORCH_FUEL_COMPONENT, new TorchFuelComponent()).getFuel();
    }

    public TorchFireState getTorchState()
    {
        return torchState;
    }

    public ModTorchHandler getHandler()
    {
        return handler;
    }

    public static ItemStack changedCopy(ItemStack stack, Item replacementItem) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack itemStack = new ItemStack(replacementItem, 1);
        stack.decrement(1);
        if (stack.getComponents() != null) {
            stack.getComponents().forEach((component) -> {
                itemStack.getComponents().copy(component.type());
            });
        }

        return itemStack;
    }

    public static ItemStack addFuel(ItemStack stack, World world, int amount) {

        if (stack.getItem() instanceof CrudeTorchItem torchItem && !world.isClient) {
            int fuel = getFuel(stack);
            if (torchItem.getComponents().contains(ModComponents.TORCH_FUEL_COMPONENT)) {
                //stack.set(ModComponents.TORCH_FUEL_COMPONENT, fuel);
                stack.set(ModComponents.TORCH_FUEL_COMPONENT, new TorchFuelComponent(fuel));
            }

            fuel += amount;

            // if burned out
            if (fuel <= 0) {
                stack = stateStack(stack, TorchFireState.BURNED_OUT);
            } else {
                if (fuel > FUEL_TIME) {
                    fuel = FUEL_TIME;
                }
                //stack.set(ModComponents.TORCH_FUEL_COMPONENT, fuel);
                stack.set(ModComponents.TORCH_FUEL_COMPONENT, new TorchFuelComponent(fuel));

            }
        }

        return stack;
    }

    @Override
    public boolean getCanItemStartFireOnUse(ItemStack stack) {
        return torchState == TorchFireState.LIT || torchState == TorchFireState.SMOULDER;
    }
}