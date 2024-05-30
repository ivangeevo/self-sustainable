package net.ivangeevo.self_sustainable.block.entity.util;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class CampfireExtinguisher
{
    public static void handleExtinguishing(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBlockEntity) {
        CampfireBlockEntityAdded fire = campfireBlockEntity;

        final int tickBurningFor = fire.getLitTime() + 1;
        fire.setLitTime(tickBurningFor);

        int burnTime = 13000; // 11.23 minutes

        if (tickBurningFor > burnTime && world.setBlockState(pos, state.with(CampfireBlock.LIT, false)))
        {
            fire.setLitTime(0);
            CampfireBlock.extinguish(null, world, pos, state);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        else if (tickBurningFor % 300 == 0)
        {
            campfireBlockEntity.markDirty();
        }
    }
}
