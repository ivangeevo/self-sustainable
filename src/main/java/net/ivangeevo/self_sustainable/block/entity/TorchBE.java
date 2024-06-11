package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.AbstractTorchBlock;
import net.ivangeevo.self_sustainable.block.entity.util.FuelBurningBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class TorchBE extends BlockEntity
{
    protected int fuel;
    protected static Random random = new Random();
    public TorchBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TORCH, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, TorchBE be)
    {
        if (!world.isClient)
        {
            if (!(state.getBlock() instanceof AbstractTorchBlock)) return;
            if (((AbstractTorchBlock) state.getBlock()).getFireState() == TorchFireState.LIT)
            {
                tickLit(world, pos, state, be);
            }
            else if (((AbstractTorchBlock) state.getBlock()).getFireState() == TorchFireState.SMOULDER)
            {
                tickSmoldering(world, pos, state, be);
            }
        }
    }

    private static void tickLit(World world, BlockPos pos, BlockState state, TorchBE be) {

        // Extinguish
        if (world.hasRain(pos))
        {
            if (random.nextInt(200) == 0)
            {
                ((AbstractTorchBlock) world.getBlockState(pos).getBlock()).extinguish(world, pos, state);
            }
        }

        // Burn out
        if (be.fuel > 0) {
            be.fuel--;

            if (be.fuel <= 0) {
                ((AbstractTorchBlock) world.getBlockState(pos).getBlock()).outOfFuel(world, pos, state, false);
            }
        }

        be.markDirty();
    }

        private static void tickSmoldering(World world, BlockPos pos, BlockState state, TorchBE be) {

        // Burn out
        if (random.nextInt(3) == 0) {
            if (be.fuel > 0) {
                be.fuel--;

                if (be.fuel <= 0) {
                    ((AbstractTorchBlock) world.getBlockState(pos).getBlock()).burnOut(world, pos, state, false);
                }
            }
        }

        be.markDirty();
    }


    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        // Save the current value of the number to the tag
        nbt.putInt("Fuel", fuel);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

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

    public void changeFuel(int increment) {
        World world = this.getWorld();
        BlockPos pos = this.getPos();

        fuel += increment;

        if (fuel <= 0) {
            fuel = 0;

            if (world.getBlockState(pos).getBlock() instanceof FuelBurningBlock) {
                FuelBurningBlock block = (FuelBurningBlock) world.getBlockState(pos).getBlock();
                block.outOfFuel(world, pos, world.getBlockState(pos), false);
            }
        }
    }
}