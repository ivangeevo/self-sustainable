package net.ivangeevo.self_sustainable.item.items;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.ivangeevo.self_sustainable.block.blocks.AbstractModTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

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
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(3.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        boolean oldHasFuel = oldStack.getComponents().contains(ModComponents.TORCH_FUEL_COMPONENT);
        boolean newHasFuel = newStack.getComponents().contains(ModComponents.TORCH_FUEL_COMPONENT);

        // If one stack has the component and the other does not, trigger animation
        return oldHasFuel != newHasFuel;
    }


    /**
    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        NbtCompound oldNbt = null;
        NbtCompound newNbt = null;

        if (oldStack.getNbt() != null) {
            oldNbt = oldStack.getNbt().copy();
            oldNbt.remove("Fuel");
        }

        if (newStack.getNbt() != null) {
            newNbt = newStack.getNbt().copy();
            newNbt.remove("Fuel");
        }

        if (oldNbt == null && newNbt != null) return true;

        return oldNbt != null && newNbt == null;
    }
     **/

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
                    // No lighting on unlit fires etc.
                    if (state.contains(Properties.LIT))
                        if (!state.get(Properties.LIT))
                            return super.useOnBlock(context);

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

    /**
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference)
    {
        // If you are clicking on it with a non HCTorch item or with empty, use vanilla behavior
        if (!slot.canTakePartial(player) || !(otherStack.getItem() instanceof CrudeTorchItem) || otherStack.isEmpty()) {
            return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
        }

        // Return left click if either is full
        if (clickType != ClickType.RIGHT && (stack.getCount() >= stack.getMaxCount() || otherStack.getCount() >= otherStack.getMaxCount()))
        {
            return false;
        }

        // Ensure torches are in same group
        if (!sameTorchGroup((CrudeTorchItem) stack.getItem(), (CrudeTorchItem) otherStack.getItem())) {
            return false;
        }

        if (((CrudeTorchItem) stack.getItem()).torchState == TorchFireState.LIT) {
            // If clicked is lit, return if clicked with burnt
            if (((CrudeTorchItem) otherStack.getItem()).torchState == TorchFireState.BURNED_OUT) {
                return false;
            }
        } else if (((CrudeTorchItem) stack.getItem()).torchState == TorchFireState.UNLIT) {
            // If clicked is unlit, return if clicked is not unlit
            if (((CrudeTorchItem) otherStack.getItem()).torchState != TorchFireState.UNLIT) {
                return false;
            }
        }

        if (!otherStack.isEmpty()) {
            int max = stack.getMaxCount();
            int usedCount = clickType != ClickType.RIGHT ? otherStack.getCount() : 1;
            int otherMax = otherStack.getMaxCount();

            int remainder = Math.max(0, usedCount - (max - stack.getCount()));
            int addedNew = usedCount - remainder;

            // Average both stacks
            int stack1Fuel = getFuel(stack) * stack.getCount();
            int stack2Fuel = getFuel(otherStack) * addedNew;
            int totalFuel = stack1Fuel + stack2Fuel;

            int updatedFuelAmount = totalFuel / (stack.getCount() + addedNew);

            if (addedNew > 0) {
                stack.increment(addedNew);
                stack.set(ModComponents.TORCH_FUEL_COMPONENT, updatedFuelAmount);
                otherStack.setCount(otherStack.getCount() - addedNew);

                return true;
            }
        }

        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }
    **/

    public boolean sameTorchGroup(CrudeTorchItem item1, CrudeTorchItem item2) {
        return item1.getHandler() == item2.getHandler();
    }

    public static Item stateItem(Item inputItem, TorchFireState newState) {
        Item outputItem = Items.AIR;

        if (inputItem instanceof CrudeTorchItem) {
            AbstractModTorchBlock newBlock = (AbstractModTorchBlock) ((BlockItem)inputItem).getBlock();

            outputItem = newBlock.handler.getStandingTorch(newState).asItem();
        }

        return outputItem;
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
        return stack.getOrDefault(ModComponents.TORCH_FUEL_COMPONENT, FUEL_TIME);
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
                stack.set(ModComponents.TORCH_FUEL_COMPONENT, fuel);
            }

            fuel += amount;

            // if burned out
            if (fuel <= 0) {
                stack = stateStack(stack, TorchFireState.BURNED_OUT);
            } else {
                if (fuel > FUEL_TIME) {
                    fuel = FUEL_TIME;
                }
                stack.set(ModComponents.TORCH_FUEL_COMPONENT, fuel);
            }
        }

        return stack;
        /**
        if (stack.getItem() instanceof  TorchItem && !world.isClient) {
            NbtCompound nbt = stack.getNbt();
            int fuel = FUEL_TIME;

            if (nbt != null) {
                fuel = nbt.getInt("Fuel");
            } else {
                nbt = new NbtCompound();
            }

            fuel += amount;

            // If burn out
            if (fuel <= 0) {
                stack = stateStack(stack, TorchFireState.BURNED_OUT);

            } else {
                if (fuel > FUEL_TIME) {
                    fuel = FUEL_TIME;
                }

                nbt.putInt("Fuel", fuel);
                stack.setNbt(nbt);
            }
        }

        return stack;
         **/
    }

}