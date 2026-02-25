package org.btwr.self_sustainable.block.entity;

import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import org.btwr.self_sustainable.block.blocks.AbstractPrimitiveStorageBlock;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.state.property.Properties.OPEN;

public class WickerBasketBE extends AbstractPrimitiveStorageBE {

    public WickerBasketBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WICKER_BASKET, pos, state);
    }

    @Override
    public void updateOpen(BlockState state, BlockPos pos, BlockHitResult hit) {
        double relativeClickY = hit.getPos().getY() - pos.getY();
        boolean isClickingLid = relativeClickY > AbstractPrimitiveStorageBlock.BASKET_OPEN_HEIGHT;

        if (!state.get(OPEN)) {
            this.setOpen(state, true);
            this.playOpeningSound(state);
        } else if (isClickingLid) {
            this.setOpen(state, false);
            this.playClosingSound(state);
        }
    }

    @Override
    protected int getInventorySize() {
        return 4;
    }

    @Override
    protected void playOpeningSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                SoundEvents.BLOCK_GRAVEL_STEP,
                0.25F + (world.getRandom().nextFloat() * 0.1F),
                0.5F + (world.getRandom().nextFloat() * 0.1F)
        );
    }

    @Override
    protected void playClosingSound(BlockState state) {
        if (this.world == null) return;
        this.playSoundTowardsFacing(
                this.world,
                state,
                SoundEvents.BLOCK_GRAVEL_STEP,
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
}