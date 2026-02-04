package org.btwr.self_sustainable.mixin.item;

import net.minecraft.block.Block;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.btwr.self_sustainable.item.util.IFirestarterItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin extends Item implements IFirestarterItem {

    public FlintAndSteelItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing) {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if (targetBlock != null && targetBlock.btwr$getCanBeSetOnFireDirectlyByItem(world, pos)) {
            return targetBlock.btwr$setOnFireDirectly(world, pos);
        }

        return false;
    }
}
