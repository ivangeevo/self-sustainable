package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.AbstractExtinguishingTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.component.ModComponentsTypes;
import net.ivangeevo.self_sustainable.item.component.TorchFuelComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TorchBE extends BlockEntity {

    static public final int MAX_BURN_TIME = 24000; // full day
    static public final int SPUTTER_TIME = 30 * 20; // 30 seconds

    protected static Random random = new Random();

    public TorchBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TORCH, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TorchBE be) {
        TorchFuelComponent fuelComponent = be.getComponents().getOrDefault(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent());

        if (world.isClient) return;
        if (!(state.getBlock() instanceof AbstractExtinguishingTorchBlock torchBlock)) return;
        if (torchBlock.getFireState() == TorchFireState.LIT) {
            tickLit(world, pos, state, be, fuelComponent);
        } else if (torchBlock.getFireState() == TorchFireState.SMOULDER) {
            tickSmoldering(world, pos, state, be, fuelComponent);
        }
    }

    private static void tickLit(World world, BlockPos pos, BlockState state, TorchBE be, TorchFuelComponent fuelComponent) {
        // Extinguish in rain
        if (world.hasRain(pos)) {
            if (random.nextInt(200) == 0) {
                ((AbstractExtinguishingTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, true);
            }
        }

        int fuel = fuelComponent.getFuel();

        // Burn out
        if (fuel > 0) {
            if (fuel < SPUTTER_TIME) {
                if (world.getBlockState(pos).getBlock() instanceof AbstractExtinguishingTorchBlock) {
                    ((AbstractExtinguishingTorchBlock) world.getBlockState(pos).getBlock()).smoulder(world, pos, state);
                }
            }
            fuelComponent.decrement();
        } else {
            if (world.getBlockState(pos).getBlock() instanceof AbstractExtinguishingTorchBlock) {
                ((AbstractExtinguishingTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, false);
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
                ((AbstractExtinguishingTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, false);
            }
        }

        be.markDirty();
    }


}
