package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.AbstractModTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.component.ModComponents;
import net.ivangeevo.self_sustainable.item.component.TorchFuelComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TorchBE extends BlockEntity {
    protected static Random random = new Random();
    public TorchBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TORCH, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TorchBE be) {
        TorchFuelComponent fuelComponent = be.getComponents().get(ModComponents.TORCH_FUEL_COMPONENT);

        if (fuelComponent == null) {
            return;
        }

        if (!world.isClient) {
            if (!(state.getBlock() instanceof AbstractModTorchBlock torchBlock)) return;
            if (torchBlock.getFireState() == TorchFireState.LIT) {
                tickLit(world, pos, state, be, fuelComponent);
            } else if (torchBlock.getFireState() == TorchFireState.SMOULDER) {
                tickSmoldering(world, pos, state, be, fuelComponent);
            }
        }
    }

    private static void tickLit(World world, BlockPos pos, BlockState state, TorchBE be, TorchFuelComponent fuelComponent) {

        // Extinguish in rain
        if (world.hasRain(pos)) {
            if (random.nextInt(200) == 0) {
                ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, true);
            }
        }

        int fuel = fuelComponent.getFuel();
        // Burn out
        if (fuel > 0) {
            fuelComponent.decrement();
        } else {
            // Ensure it only applies to torches placed in the world
            if (world.getBlockState(pos).getBlock() instanceof AbstractModTorchBlock) {
                ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).outOfFuel(world, pos, state, false);
            }
        }


        be.markDirty();
    }

    private static void tickSmoldering(World world, BlockPos pos, BlockState state, TorchBE be, TorchFuelComponent fuelComponent) {
        int fuel = fuelComponent.getFuel();

        // Burn out
        if (random.nextInt(3) == 0) {
            if (fuel > 0) {
                fuelComponent.decrement();
            } else {
                ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, false);
            }
        }

        be.markDirty();
    }


}
