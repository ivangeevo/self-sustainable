package org.btwr.self_sustainable.block.entity;

import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

public class SmokerOvenBE extends AbstractOvenBE {

    public SmokerOvenBE(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.SMOKER_BRICK, pos, state);
    }

    @Override
    public void tick() {

    }
}