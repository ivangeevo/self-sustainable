package org.btwr.self_sustainable.item.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.btwr.self_sustainable.tag.ModTags;

import static net.minecraft.state.property.Properties.LIT;

public interface IgnitableTorchItem {

    void lightTorch(ItemStack stack, World world, BlockPos pos, PlayerEntity player, Hand hand);

     default boolean isIgnitionSource(BlockState state) {
        return state.isIn(ModTags.Blocks.DIRECTLY_IGNITES_ITEM_ON_USE) || (state.contains(LIT) && state.get(LIT));
    }

    // Returns the fire BlockPos if the player is looking at fire, null otherwise
    default BlockPos findFireInSight(PlayerEntity player, World world) {
        Vec3d start = player.getCameraPosVec(1.0f);
        Vec3d look = player.getRotationVec(1.0f);
        double range = player.getBlockInteractionRange();

        for (double t = 0; t <= 1.0; t += 0.02) {
            Vec3d point = start.add(look.multiply(range * t));
            BlockPos checkPos = BlockPos.ofFloored(point);
            if (isIgnitionSource(world.getBlockState(checkPos))) {
                return checkPos;
            }
        }
        return null;
    }
}
