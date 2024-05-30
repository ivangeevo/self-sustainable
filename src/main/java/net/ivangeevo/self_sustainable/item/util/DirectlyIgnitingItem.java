package net.ivangeevo.self_sustainable.item.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface DirectlyIgnitingItem
{

     boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing);



}
