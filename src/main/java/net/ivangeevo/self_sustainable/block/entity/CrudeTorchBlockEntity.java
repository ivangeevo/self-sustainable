/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Crude Torches have the same functionality as
 * normal ones, except that they have a chance to get destroyed in rain. **/
public class CrudeTorchBlockEntity extends BlockEntity implements Ignitable
{


    public CrudeTorchBlockEntity(BlockPos pos, BlockState state)
    {
        super( ModBlockEntities.CRUDE_TORCH, pos, state );
    }
    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.CRUDE_TORCH;
    }
}
