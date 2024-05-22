package net.ivangeevo.self_sustainable.block.entity.util;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static net.minecraft.state.property.Properties.LIT;

public abstract class TorchExtinguisher
{


    public static class Item {
        public static void onLitServerTick(World world, BlockPos pos, BlockState state, TorchBlockEntity torch) {
            final int tickBurningFor = torch.getLitTime() + 1;
            // Add item-specific logic here
        }
    }


    public static class Block {
        public static void onServerTick(World world, BlockPos pos, BlockState state, BlockEntity entity)
        {
            TorchBlockEntity torchBE = (TorchBlockEntity) entity;
            final int tickBurningFor = torchBE.getLitTime() + 1;

            // 5% chance for crude torches to extinguish due to rain
            if ((state.isOf(ModBlocks.CRUDE_TORCH) || state.isOf(ModBlocks.WALL_CRUDE_TORCH))
                    && world.hasRain(pos) && world.random.nextFloat() < 0.05f)
            {
                torchBE.setLitTime(0);
                world.setBlockState(pos, state.with(LIT, false));
            }

            int burnTime = 24; // 20 minutes

            if (tickBurningFor > burnTime && world.setBlockState(pos, state.with(LIT, false)))
            {
                torchBE.setLitTime(0);
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                // Rest of the common logic
            }
            else if (tickBurningFor % 300 == 0)
            {
                torchBE.markDirty();
            }
        }


        public static void onClientTick(World world, BlockPos pos, BlockState state, TorchBlockEntity torch)
        {

        }
    }




}
