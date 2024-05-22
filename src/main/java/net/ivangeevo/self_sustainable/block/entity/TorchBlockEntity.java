/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.entity.util.TorchExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.interfaces.TorchBlockAdded;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;


public class TorchBlockEntity extends BlockEntity implements Ignitable
{
    @Unique private boolean lit;
    @Unique protected int litTime = 0;
    public int getLitTime() {
        return litTime;
    }
    public void setLitTime(int value) {
        this.litTime = value;
    }

    public TorchBlockEntity(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.TORCH, pos, state);
        lit = state.get(LIT);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, TorchBlockEntity oven)
    {
        TorchExtinguisher.Block.onServerTick(world, pos, state, oven);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, TorchBlockEntity oven)
    {
        TorchExtinguisher.Block.onClientTick(world, pos, state, oven);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.getBoolean("Lit")) { lit = nbt.getBoolean("Lit"); }
        if (nbt.contains("LitTime")) { litTime = nbt.getInt("LitTime"); }

    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putBoolean("Lit", lit);
        nbt.putInt("LitTime", litTime);
    }

    public ItemEntity asItemEntity() {
        return new ItemEntity(EntityType.ITEM, world);
    }

    public ItemEntity asItem(Item entity) {
        return asItemEntity();
    }



}
