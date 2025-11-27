package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.UnfiredBrickBlock;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class UnfiredBrickBE extends BlockEntity {

    protected int dryingLevel = 0;

    private static final int TIME_TO_COOK = (10 * 60 * 20);
    private static final int RAIN_COOK_DECAY = 10;

    private boolean isDrying = false;

    public UnfiredBrickBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BRICK_UNFIRED, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.dryingLevel = nbt.getShort("DryingTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("DryingTime", (short) this.dryingLevel);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public static void tick(World world, BlockPos pos, BlockState state, UnfiredBrickBE be) {
        if ( !world.isClient ) be.updateDrying();
    }

    public void updateDrying() {
        boolean bNewDrying;

        assert world != null;
        bNewDrying = world.getLightLevel(pos) >= 15;

        BlockState stateAbove = world.getBlockState( pos.up() );
        Block blockAbove = stateAbove.getBlock();

        /**
        if ( blockAbove != null && stateAbove.isIn(BTWRTags.Blocks.GROUND_COVERS)) {
            bNewDrying = false;
        }
         **/

        if (bNewDrying != isDrying) {
            isDrying = bNewDrying;

            world.markDirty( pos );
        }

        UnfiredBrickBlock brickBlock = (UnfiredBrickBlock) ModBlocks.BRICK_UNFIRED;

        if (isDrying) {
            dryingLevel++;

            if (dryingLevel >= TIME_TO_COOK) {
                brickBlock.onFinishedCooking(world, pos, world.getBlockState(pos));

                return;
            }
        }
        else {
            if ( isRainingOnBrick(world, pos) ) {
                dryingLevel -= RAIN_COOK_DECAY;

                if (dryingLevel < 0 ) {
                    dryingLevel = 0;
                }
            }
        }

        int displayedDryLevel = brickBlock.getDryLevel(world, pos);
        int currentDryLevel = computeDryLevel();

        if (displayedDryLevel != currentDryLevel) {
            UnfiredBrickBlock.setDryingLevel(world, pos, currentDryLevel);
        }
    }

    public boolean isRainingOnBrick(World world, BlockPos pos)
    {
        return world.isRaining() && world.hasRain(pos);
    }

    private int computeDryLevel() {
        if (dryingLevel > 0 ) {
            int iCookLevel = (int)(((float) dryingLevel / (float) TIME_TO_COOK) * 7F ) + 1;

            return MathHelper.clamp( iCookLevel, 0, 7 );
        }

        return 0;
    }

}