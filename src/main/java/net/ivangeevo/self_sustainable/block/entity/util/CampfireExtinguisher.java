package net.ivangeevo.self_sustainable.block.entity.util;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CampfireExtinguisher
{
    public static void onLitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBlockEntity) {
        CampfireBlockEntityAdded accessCampfire = (CampfireBlockEntityAdded) campfireBlockEntity;

        final int tickBurningFor = accessCampfire.getLitTime() + 1;
        accessCampfire.setLitTime(tickBurningFor);

        int burnTime = 13000; // 10.83 minutes

        if (tickBurningFor > burnTime && world.setBlockState(pos, state.with(CampfireBlock.LIT, false)))
        {
            accessCampfire.setLitTime(0);
            CampfireBlock.extinguish(null, world, pos, state);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
        else if (tickBurningFor % 300 == 0)
        {
            campfireBlockEntity.markDirty();
        }
    }
}
