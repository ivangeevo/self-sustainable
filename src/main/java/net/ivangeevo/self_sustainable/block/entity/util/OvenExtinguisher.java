package net.ivangeevo.self_sustainable.block.entity.util;

import net.ivangeevo.self_sustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public abstract class OvenExtinguisher
{
    public static void onLitServerTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity brickOvenBlockEntity)
    {

        final int tickBurningFor = brickOvenBlockEntity.getLitTime() + 1;
        brickOvenBlockEntity.setLitTime(tickBurningFor);

        int burnTime = 36000; // 30 minutes

        if (tickBurningFor > burnTime && world.setBlockState(pos, state.with(CampfireBlock.LIT, false)))
        {
            brickOvenBlockEntity.setLitTime(0);
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        else if (tickBurningFor % 300 == 0)
        {
            brickOvenBlockEntity.markDirty();
        }
    }
}
