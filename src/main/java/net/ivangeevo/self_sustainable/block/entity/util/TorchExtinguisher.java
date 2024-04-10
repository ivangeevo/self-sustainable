package net.ivangeevo.self_sustainable.block.entity.util;

import net.ivangeevo.self_sustainable.ModItems;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.TorchBlock;
import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class TorchExtinguisher {

    /**
    public static class Item {
        public static void onLitServerTick(World world, BlockPos pos, BlockState state, TorchBlockEntity torch) {
            final int tickBurningFor = torch.getLitTime() + 1;
            // Add item-specific logic here
        }
    }

    public static class Block {
        public static void serverTick(World world, BlockPos pos, BlockState state, TorchBlockEntity torch)
        {
            final int tickBurningFor = torch.getLitTime() + 1;
            // Add common logic here
            PlayerEntity player = world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), -1.0, false);

            if (player != null && player.isSubmergedInWater())
            {
                // Drop an item entity of the passed item
                ItemStack unlitTorch;
                if (state.isOf(ModBlocks.CRUDE_TORCH) || state.isOf(ModBlocks.WALL_CRUDE_TORCH))
                {
                    unlitTorch = new ItemStack(ModItems.CRUDE_TORCH);
                }
                else if (state.isOf(Blocks.SOUL_TORCH) || state.isOf(Blocks.SOUL_WALL_TORCH))
                {
                    unlitTorch = new ItemStack(Items.SOUL_TORCH);
                }
                else
                {
                    unlitTorch = new ItemStack(ModItems.CRUDE_TORCH);
                }
                world.spawnEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), unlitTorch));
            }

            // 5% chance for crude torches to extinguish due to rain
            if ((state.isOf(ModBlocks.CRUDE_TORCH) || state.isOf(ModBlocks.WALL_CRUDE_TORCH))
                    && world.hasRain(pos) && world.random.nextFloat() < 0.05f)
            {
                torch.setLitTime(0);
                world.setBlockState(pos, state.with(TorchBlock.LIT, false));
            }
            int burnTime = 24; // 20 minutes
            if (tickBurningFor > burnTime && world.setBlockState(pos, state.with(TorchBlock.LIT, false)))
            {
                torch.setLitTime(0);
                world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
                // Rest of the common logic
            }
            else if (tickBurningFor % 300 == 0)
            {
                torch.markDirty();
            }
        }
    }
     **/


}
