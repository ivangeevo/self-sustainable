package net.ivangeevo.self_sustainable.item.util;

import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface DirectlyIgnitingItem
{

     float CHANCE_DECAY_PER_TICK = 0.00025F;
     long DELAY_BEFORE_DECAY = (2 * 20 ); // two seconds


     boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing);

     void performUseEffects(ItemUsageContext context);

     boolean checkChanceOfStart(ItemStack stack, Random random);
}


