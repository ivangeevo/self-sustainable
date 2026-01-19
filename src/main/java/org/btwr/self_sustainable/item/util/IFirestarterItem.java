package org.btwr.self_sustainable.item.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface IFirestarterItem {

     float CHANCE_DECAY_PER_TICK = 0.00025F;
     long DELAY_BEFORE_DECAY = (2 * 20 ); // two seconds

     boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing);

     void performUseEffects(ItemUsageContext context);

     boolean checkChanceOfStart(ItemStack stack, Random random);

}