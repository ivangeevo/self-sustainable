package org.btwr.self_sustainable.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.btwr.self_sustainable.block.blocks.AbstractBasketBlock;
import org.btwr.self_sustainable.block.blocks.HamperBlock;
import org.btwr.self_sustainable.block.blocks.WickerBasketBlock;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.btwr.self_sustainable.util.TickableBlockEntity;

import static net.minecraft.state.property.Properties.OPEN;

public class HamperBE extends LootableContainerBlockEntity implements TickableBlockEntity {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(18, ItemStack.EMPTY);

    protected float animationAngle, lastAnimationAngle;

    public HamperBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HAMPER, pos, state);
    }

    private final ViewerCountManager stateManager = new ViewerCountManager() {
        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            HamperBE.this.playSoftSound(state);
            HamperBE.this.setOpen(state, true);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            HamperBE.this.playHarshSound(state);
            HamperBE.this.setOpen(state, false);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        }

        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler genericHandler) {
                Inventory inventory = genericHandler.getInventory();
                return inventory == HamperBE.this;
            } else {
                return false;
            }
        }
    };

    public void updateOpen(BlockState state) {
        if (!state.get(OPEN)) {
            this.setOpen(state, true);
            this.playSoftSound(state);
        } else {
            this.setOpen(state, false);
            this.playHarshSound(state);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        if (!this.writeLootTable(nbt)) {
            Inventories.writeNbt(nbt, this.inventory, registryLookup);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.readLootTable(nbt)) {
            Inventories.readNbt(nbt, this.inventory, registryLookup);
        }

    }

    @Override
    protected Text getContainerName() {
        return Text.translatable("container.hamper");
    }

    @Override
    public DefaultedList<ItemStack> getHeldStacks() {
        return this.inventory;
    }

    @Override
    protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_3X3, syncId, playerInventory, this, 1);
    }

    @Override
    public int size() {
        return 9;
    }

    public void tick() {
        if (!this.removed) {
            this.stateManager.updateViewerCount(this.getWorld(), this.getPos(), this.getCachedState());
        }

        if (world != null && world.isClient) {
            boolean isOpen = this.getCachedState().get(OPEN);
            this.lastAnimationAngle = this.animationAngle;

            if (isOpen && this.animationAngle < 1 || !isOpen && this.animationAngle > 0) {
                this.animationAngle += isOpen ? 0.1F : -0.2F;
                this.animationAngle = MathHelper.clamp(this.animationAngle, 0, 1F);
            }
        }
    }

    public float getAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.lastAnimationAngle, this.animationAngle);
    }

    void setOpen(BlockState state, boolean open) {
        if (this.world != null) {
            this.world.setBlockState(this.getPos(), state.with(OPEN, open), Block.NOTIFY_ALL_AND_REDRAW);
        }
    }

    public void playSoftSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                SoundEvents.BLOCK_GRAVEL_STEP,
                0.25F + (world.getRandom().nextFloat() * 0.1F),
                0.5F + (world.getRandom().nextFloat() * 0.1F)
        );
    }

    public void playHarshSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                SoundEvents.BLOCK_GRAVEL_STEP,
                0.1F + (world.getRandom().nextFloat() * 0.1F),
                1F + (world.getRandom().nextFloat() * 0.25F)
        );
    }

    protected void playSoundTowardsFacing(World world, BlockState state, SoundEvent soundEvent, float volume, float pitch) {
        Vec3i vec3i = (state.get(AbstractBasketBlock.FACING)).getVector();

        double d = this.pos.getX() + 0.5 + vec3i.getX() / 2.0;
        double e = this.pos.getY() + 0.5 + vec3i.getY() / 2.0;
        double f = this.pos.getZ() + 0.5 + vec3i.getZ() / 2.0;

        world.playSound(null, d ,e ,f, soundEvent, SoundCategory.BLOCKS, volume, pitch);
    }

    public Direction getHorizontalFacing() {
        return this.getCachedState().get(Properties.HORIZONTAL_FACING);
    }

}