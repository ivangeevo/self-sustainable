package org.btwr.self_sustainable.block.entity;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.btwr.self_sustainable.block.blocks.WickerBasketBlock;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.btwr.self_sustainable.sound.ModSoundEvents;

import static net.minecraft.state.property.Properties.OPEN;

public class WickerBasketBE extends AbstractBasketBE {

    public WickerBasketBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WICKER_BASKET, pos, state);
    }

    @Override
    public void updateOpen(BlockState state, BlockPos pos, BlockHitResult hit) {
        if (!state.get(OPEN)) {
            this.setOpen(state, true);
            this.playOpenSound(state);
        } else if (WickerBasketBlock.isClickingLid(hit)) {
            this.setOpen(state, false);
            this.playCloseSound(state);
        }
    }

    @Override
    protected int getInventorySize() {
        return 4;
    }

    @Override
    public void playOpenSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                ModSoundEvents.WICKER_BASKET_OPEN,
                0.25F + (world.getRandom().nextFloat() * 0.1F),
                0.5F + (world.getRandom().nextFloat() * 0.1F)
        );
    }

    @Override
    public void playCloseSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                ModSoundEvents.WICKER_BASKET_CLOSE,
                0.1F + (world.getRandom().nextFloat() * 0.1F),
                1F + (world.getRandom().nextFloat() * 0.25F)
        );
    }

    @Override
    public void playInsertSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                ModSoundEvents.WICKER_BASKET_INSERT_ITEM,
                0.25F + (world.getRandom().nextFloat() * 0.1F),
                0.5F + (world.getRandom().nextFloat() * 0.1F)
        );
    }

    @Override
    public void playTakeOutSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                ModSoundEvents.WICKER_BASKET_TAKE_OUT_ITEM,
                0.1F + (world.getRandom().nextFloat() * 0.1F),
                1F + (world.getRandom().nextFloat() * 0.25F)
        );
    }

    @Override
    public void tick() {
        if (world != null && world.isClient) {
            boolean isOpen = this.getCachedState().get(OPEN);
            this.lastAnimationAngle = this.animationAngle;

            if (isOpen && this.animationAngle < 1 || !isOpen && this.animationAngle > 0) {
                this.animationAngle += isOpen ? 0.1F : -0.2F;
                this.animationAngle = MathHelper.clamp(this.animationAngle, 0, 1F);
            }
        }
    }

    public void dropStack(ItemStack stack, float yOffset) {
        if (this.world != null) {
            Vec3d pos = this.getPos().toCenterPos();
            ItemEntity itemEntity = new ItemEntity(
                    this.world, pos.getX(),
                    pos.getY() + yOffset, pos.getZ(),
                    stack);
            itemEntity.setToDefaultPickupDelay();
            this.world.spawnEntity(itemEntity);
        }
    }
}