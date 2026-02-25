package org.btwr.self_sustainable.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.blocks.AbstractPrimitiveStorageBlock;
import org.btwr.self_sustainable.util.TickableBlockEntity;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.HORIZONTAL_FACING;
import static net.minecraft.state.property.Properties.OPEN;

/**
 * Adapted directly from Primitive Storage (CC0).
 *
 * <p>Original project:
 * <a href="https://github.com/jeffinitup/primitive-storage/">
 * https://github.com/jeffinitup/primitive-storage/
 * </a>
 *
 * <p>Original author:
 * JeffyJamzHD
 *
 */
public abstract class AbstractPrimitiveStorageBE extends BlockEntity implements Inventory, TickableBlockEntity {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(getInventorySize(), ItemStack.EMPTY);

    protected float animationAngle, lastAnimationAngle;

    public AbstractPrimitiveStorageBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            syncInventory();
            serverWorld.getChunkManager().markForUpdate(pos);
        }
        this.updateListeners();
    }

    @Override
    public int size() {
        return getInventorySize();
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.inventory.remove(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return this.inventory.get(slot).split(amount);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    public DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    public void setHeldStacks(DefaultedList<ItemStack> stacks) {
        this.inventory = stacks;
    }

    public void updateListeners() {
        if (this.getWorld() == null) return;

        this.getWorld().updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL_AND_REDRAW);
        this.getWorld().updateComparators(pos, getCachedState().getBlock());
    }

    public abstract void updateOpen(BlockState state, BlockPos pos, BlockHitResult hit);

    protected void setOpen(BlockState state, boolean open) {
        if (this.world != null) {
            this.world.setBlockState(this.getPos(), state.with(OPEN, open), Block.NOTIFY_ALL_AND_REDRAW);
        }
    }

    public void syncInventory() {
        if (this.getWorld() != null && !this.getWorld().isClient()) {
            AbstractPrimitiveStorageBlock.sendSyncPacket(this.getWorld(), this.getPos(), this.getInventory());
        }
    }

    public void scheduledTick() {
        if (this.getWorld() != null) {
            this.getWorld().scheduleBlockTick(this.getPos(), this.getCachedState().getBlock(), 2);
        }
    }

    public float getAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAnimationAngle, this.animationAngle);
    }

    public Direction getHorizontalFacing() {
        return this.getCachedState().get(HORIZONTAL_FACING);
    }

    protected void playSoundTowardsFacing(World world, BlockState state, SoundEvent soundEvent, float volume, float pitch) {
        Vec3i vec3i = (state.get(AbstractPrimitiveStorageBlock.FACING)).getVector();

        double d = this.pos.getX() + 0.5 + vec3i.getX() / 2.0;
        double e = this.pos.getY() + 0.5 + vec3i.getY() / 2.0;
        double f = this.pos.getZ() + 0.5 + vec3i.getZ() / 2.0;

        world.playSound(null, d ,e ,f, soundEvent, SoundCategory.BLOCKS, volume, pitch);
    }

    protected DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    protected abstract int getInventorySize();

    /** Play opening sound based on a direction **/
    protected abstract void playOpeningSound(BlockState state);

    /** Play closing sound based on a direction **/
    protected abstract void playClosingSound(BlockState state);
}
