/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.CrudeTorchBlock;
import net.ivangeevo.self_sustainable.block.blocks.TorchBlock;
import net.ivangeevo.self_sustainable.block.entity.util.TorchExtinguisher;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Unique;

public class TorchBlockEntity extends BlockEntity  {


    @Unique public boolean lit;
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
        lit = state.get(TorchBlock.LIT);
    }

    public static boolean isLit(ItemStack stack)
    {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean("Lit");
    }


    private TorchBlockEntity getTorch()
    {

        if (world == null || pos == null)
        {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TorchBlockEntity))
        {
            return null;
        }

        return (TorchBlockEntity) blockEntity;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, TorchBlockEntity oven)
    {

        //TorchExtinguisher.Block.serverTick(world, pos, state, oven);


    }

    public static void clientTick(World world, BlockPos pos, BlockState state, TorchBlockEntity oven)
    {

    }





    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        if (nbt.getBoolean("Lit")) { lit = nbt.getBoolean("Lit"); }
        if (nbt.contains("LitTime")) { litTime = nbt.getInt("LitTime"); }

    }

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
