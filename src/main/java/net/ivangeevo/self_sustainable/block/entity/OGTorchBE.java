package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.AbstractModTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class OGTorchBE extends BlockEntity {
    protected int fuel;
    protected static Random random = new Random();
    public OGTorchBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TORCH, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, OGTorchBE be) {
        if (!world.isClient) {
            if (!(state.getBlock() instanceof AbstractModTorchBlock torchBlock)) return;
            if (torchBlock.getFireState() == TorchFireState.LIT) {
                tickLit(world, pos, state, be);
            } else if (torchBlock.getFireState() == TorchFireState.SMOULDER) {
                tickSmoldering(world, pos, state, be);
            }
        }
    }

    private static void tickLit(World world, BlockPos pos, BlockState state, OGTorchBE be) {

        // Extinguish in rain
        if (world.hasRain(pos)) {
            if (random.nextInt(200) == 0) {
                ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, true);
            }
        }

        // Burn out
        if (be.fuel > 0) {
            be.fuel--;

            if (be.fuel <= 0) {
                ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).outOfFuel(world, pos, state, false);
            }
        }

        be.markDirty();
    }

    private static void tickSmoldering(World world, BlockPos pos, BlockState state, OGTorchBE be) {

        // Burn out
        if (random.nextInt(3) == 0) {
            if (be.fuel > 0) {
                be.fuel--;

                if (be.fuel <= 0) {
                    ((AbstractModTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, false);
                }
            }
        }

        be.markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Fuel", fuel);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("number")) {
            fuel = nbt.getInt("number");
        } else {
            fuel = nbt.getInt("Fuel");
        }
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int newValue) {
        fuel = newValue;
    }

}
