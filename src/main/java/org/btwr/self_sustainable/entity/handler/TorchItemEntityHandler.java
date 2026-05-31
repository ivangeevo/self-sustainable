package org.btwr.self_sustainable.entity.handler;

import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.TorchFuelComponent;
import org.btwr.self_sustainable.item.items.CrudeTorchBlockItem;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.jetbrains.annotations.NotNull;

public class TorchItemEntityHandler {

    public static void tickTorchFuel(ItemEntity entity) {
        ItemStack stack = entity.getStack();
        if (!isCrudeLitTorch(stack)) return;

        TorchFuelComponent fuelComponent = stack.getOrDefault(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent());

        if (entity.isOnGround() && entity.isAlive()) {
            fuelComponent.decrement();
        }
        if (fuelComponent.getFuel() == 0) {
            entity.discard();
        }
    }

    public static boolean tickTorchInWater(ItemEntity entity, boolean wasInFluid) {
        ItemStack stack = entity.getStack();
        if (!isTorch(stack)) return wasInFluid;

        World world = entity.getWorld();
        if (world.isClient) return wasInFluid;

        BlockPos pos = entity.getBlockPos();
        FluidState fluidState = world.getFluidState(pos);
        boolean inFluid = false;

        if (fluidState.isIn(FluidTags.WATER)) {
            // Actual fluid surface height at this block
            float fluidSurfaceY = pos.getY() + fluidState.getHeight(world, pos);

            // Check if the entity bottom is below the actual fluid surface
            inFluid = entity.getY() < fluidSurfaceY;
        }

        if (inFluid && !wasInFluid) {
            onItemLandsInWater(entity, stack, world);
        }

        return inFluid;
    }

    private static void onItemLandsInWater(ItemEntity entity, ItemStack stack, World world) {
        world.playSound(null, entity.getBlockPos(),
                ModSoundEvents.TORCH_EXTINGUISH,
                SoundCategory.BLOCKS, 0.5f, 1.0f
        );

        if (isCrudeLitTorch(stack)) {
            entity.discard();
        } else if (isInfiniteLitTorch(stack)) {
            world.spawnEntity(getInWaterDrop(entity, stack, world));
            entity.discard();
        }
    }

    private static @NotNull ItemEntity getInWaterDrop(ItemEntity entity, ItemStack stack, World world) {
        ItemStack replacement = stack.isOf(Items.TORCH)
                ? new ItemStack(ModItems.TORCH_UNLIT)
                : new ItemStack(ModItems.SOUL_TORCH_UNLIT);

        ItemEntity drop = new ItemEntity(world,
                entity.getX(), entity.getY(), entity.getZ(),
                replacement.copyWithCount(stack.getCount())
        );
        drop.setVelocity(entity.getVelocity());
        return drop;
    }

    public static boolean isTorch(ItemStack stack) {
        return isCrudeLitTorch(stack) || isInfiniteLitTorch(stack);
    }

    public static boolean isCrudeLitTorch(ItemStack stack) {
        return stack.getItem() instanceof CrudeTorchBlockItem &&
                (stack.isOf(ModItems.CRUDE_TORCH_LIT) || stack.isOf(ModItems.CRUDE_TORCH_SMOULDER));
    }

    public static boolean isInfiniteLitTorch(ItemStack stack) {
        return stack.isOf(Items.TORCH) || stack.isOf(Items.SOUL_TORCH);
    }
}