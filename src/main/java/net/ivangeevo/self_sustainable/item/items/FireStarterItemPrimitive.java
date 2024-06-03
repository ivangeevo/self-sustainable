// FCMOD

package net.ivangeevo.self_sustainable.item.items;


import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;

public class FireStarterItemPrimitive extends FireStarterItem
{

    private final float baseChance;
    private final float maxChance;
    private final float chanceIncreasePerUse;

    private static final float CHANCE_DECAY_PER_TICK = 0.00025F;
    private static final long DELAY_BEFORE_DECAY = (2 * 20 ); // two seconds

    public FireStarterItemPrimitive(Item.Settings settings, float fExhaustionPerUse, float fBaseChance, float fMaxChance, float fChanceIncreasePerUse )
    {
        super( settings, fExhaustionPerUse );
        //settings.maxDamage(iMaxUses);

        baseChance = fBaseChance;
        maxChance = fMaxChance;
        chanceIncreasePerUse = fChanceIncreasePerUse;

    }


    @Override
    protected boolean checkChanceOfStart(ItemStack stack, net.minecraft.util.math.random.Random random) {
        boolean bReturnValue = false;

        // Use this cast to get access to the new variables.
        ItemStackAdded stackAdded;
        stackAdded = stack;

        float fChance = stackAdded.getAccumulatedChance(baseChance);
        long lCurrentTime = WorldUtils.getOverworldTimeServerOnly();
        long lLastTime = stackAdded.getTimeOfLastUse();

        if ( lLastTime > 0 )
        {
            if ( lCurrentTime > lLastTime )
            {
                long lDecayTime = ( lCurrentTime - lLastTime ) - DELAY_BEFORE_DECAY;

                if ( lDecayTime > 0 )
                {
                    fChance -= (float)lDecayTime * CHANCE_DECAY_PER_TICK;

                    if (fChance < baseChance)
                    {
                        fChance = baseChance;
                    }
                }
            }
            else if ( lCurrentTime < lLastTime )
            {
                // do not reset chance if currentTime is the same as last time, in case use attempts
                // stack up on the server

                fChance = baseChance;
            }
        }

        if ( random.nextFloat() <= fChance )
        {
            bReturnValue = true;
        }

        fChance += chanceIncreasePerUse;

        if (fChance > maxChance)
        {
            fChance = maxChance;
        }

        stackAdded.setAccumulatedChance(fChance);
        stackAdded.setTimeOfLastUse(lCurrentTime);

        return bReturnValue;
    }

    @Override
    protected void performUseEffects(PlayerEntity player) {
        player.playSound(SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5f + 0.5f * (float)player.getRandom().nextInt(2), (player.getRandom().nextFloat() * 0.25f) + 1.75f);

        if (!player.getWorld().isClient()) {
            for (int var3 = 0; var3 < 5; ++var3) {
                Vec3d var4 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);

                var4 = var4.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var4 = var4.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                Vec3d var5 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.3, (-player.getRandom().nextFloat()) * 0.6 - 0.3, 0.6);

                var5 = var5.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var5 = var5.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                var5 = var5.add(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ());

                player.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getDefaultStack()), var5.getX(), var5.getY(), var5.getZ(), var4.getX(), var4.getY() + 0.05, var4.getZ());
            }
        }
    }

    @Override
    public boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing)
    {
        if ( super.attemptToLightBlock(stack, world, pos, facing) )
        {
            ((ItemStackAdded)stack).setAccumulatedChance(baseChance);

            return true;
        }

        return false;
    }


    //------------- Class Specific Methods ------------//

    //----------- Client Side Functionality -----------//

}
