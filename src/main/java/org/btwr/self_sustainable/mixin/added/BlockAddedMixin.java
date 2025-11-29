package org.btwr.self_sustainable.mixin.added;

import org.btwr.self_sustainable.block.interfaces.added.BlockAdded;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Block.class)
public abstract class BlockAddedMixin implements BlockAdded {

    @Override
    public boolean btwr$getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return btwr$getCanBeSetOnFireDirectly(blockAccess, pos);
    }

    @Override
    public boolean btwr$getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return false;
    }

    @Override
    public boolean btwr$setOnFireDirectly(World world, BlockPos pos) {
        return false;
    }

}