/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Crude Torches have the same functionality as
 * normal ones, except that they have a chance to get destroyed in rain. **/
public class CrudeTorchBlockEntity extends TorchBlockEntity  {


    public CrudeTorchBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);

    }



}
