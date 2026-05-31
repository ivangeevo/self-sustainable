package org.btwr.self_sustainable.item.items;

import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import org.btwr.self_sustainable.block.blocks.AbstractCrudeTorchBlock;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.TorchFuelComponent;
import org.btwr.self_sustainable.item.interfaces.IgnitableTorchItem;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.btwr.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CrudeTorchBlockItem extends VerticallyAttachableBlockItem implements IgnitableTorchItem {

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
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();

        if (torchState == TorchFireState.UNLIT && player != null) {
            BlockPos firePos = findFireInSight(player, world);
            if (firePos != null) {
                if (!world.isClient) {
                    lightTorch(stack, world, firePos, player, hand);
                }
                return ActionResult.SUCCESS;
            }
        }

        // Direct hit on an ignition source (campfire, lit block, etc.)
        if (state.getBlock().btwr$getCanBlockLightItemOnFire(world, pos)) {
            if (state.getBlock() instanceof BrickOvenBlock ovenBlock) {
                BlockHitResult hit = new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), context.getSide(), pos, false);
                if (torchState == TorchFireState.UNLIT && ovenBlock.isBottomPortionClick(hit, pos)) {
                    if (!world.isClient && player != null) {
                        lightTorch(stack, world, pos, player, hand);
                    }
                    return ActionResult.SUCCESS;
                }
            }
            if (torchState == TorchFireState.UNLIT) {
                if (!world.isClient && player != null) {
                    lightTorch(stack, world, pos, player, hand);
                }
                return ActionResult.SUCCESS;
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        // Handles right-click in air while looking at fire
        if (torchState == TorchFireState.UNLIT) {
            BlockPos firePos = findFireInSight(user, world);
            if (firePos != null) {
                if (!world.isClient) {
                    lightTorch(stack, world, firePos, user, hand);
                }
                return TypedActionResult.success(user.getStackInHand(hand));
            }
        }

        return super.use(world, user, hand);
    }

    @Override
    public void lightTorch(ItemStack stack, World world, BlockPos soundPos, PlayerEntity player, Hand hand) {
        ItemStack litTorch = stateStack(stack, TorchFireState.LIT);
        stack.decrement(1);

        if (stack.isEmpty()) {
            player.setStackInHand(hand, litTorch);
        } else {
            player.setStackInHand(hand, stack);
            if (!player.getInventory().insertStack(litTorch)) {
                player.dropItem(litTorch, false);
            }
        }

        world.playSound(
                null, soundPos, ModSoundEvents.TORCH_IGNITE,
                SoundCategory.BLOCKS, 0.5f, 1.2f
        );
    }

    @Override
    public boolean btwr$getCanItemStartFireOnUse(ItemStack stack) {
        return torchState == TorchFireState.LIT || torchState == TorchFireState.SMOULDER;
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

}