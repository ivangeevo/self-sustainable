package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.interfaces.BlockAdded;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockMixin implements BlockAdded
{

    public boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos)
    {
        return getCanBeSetOnFireDirectly(blockAccess, pos);
    }

    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos)
    {
        return false;
    }

}
