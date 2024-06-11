package net.ivangeevo.self_sustainable.block.entity.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface FuelBurningBlock
{

    void outOfFuel(World world, BlockPos pos, BlockState state, boolean playSound);



    default boolean tryUse(TagKey torches, ItemStack stack, PlayerEntity player, Hand hand) {

        return stack.isIn(torches);
    }

    default boolean isValidItem(ItemStack stack, TagKey torches) {

        return stack.isIn(torches);
    }
}