package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public interface Ignitable
{
    BooleanProperty LIT = Properties.LIT;

    static void playLitFX(World world, BlockPos pos)
    {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);

    }

    static void playExtinguishSound(World world, BlockPos pos, boolean isQuiet)
    {
        float fizzVolume = 0.5F;
        float fizzPitch = 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F;

        if (isQuiet) {
            fizzVolume = 0.1F;
            fizzPitch = 1F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F;
        }

        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,fizzVolume, fizzPitch);

    }
}
