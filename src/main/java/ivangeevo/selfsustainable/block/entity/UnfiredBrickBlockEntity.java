package ivangeevo.selfsustainable.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class UnfiredBrickBlockEntity extends BlockEntity {

    public int ticks;

    private static final Random RANDOM = Random.create();

    public UnfiredBrickBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.ENCHANTING_TABLE, pos, state);
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

    }

    public static void tick(World world, BlockPos pos, BlockState state, EnchantingTableBlockEntity blockEntity) {
        blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
        blockEntity.lastBookRotation = blockEntity.bookRotation;
        PlayerEntity playerEntity = world.getClosestPlayer((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 3.0, false);
        if (playerEntity != null) {
            double d = playerEntity.getX() - ((double)pos.getX() + 0.5);
            double e = playerEntity.getZ() - ((double)pos.getZ() + 0.5);
            blockEntity.targetBookRotation = (float) MathHelper.atan2(e, d);
            blockEntity.nextPageTurningSpeed += 0.1F;
            if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float f = blockEntity.flipRandom;

                do {
                    blockEntity.flipRandom += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while(f == blockEntity.flipRandom);
            }
        } else {
            blockEntity.targetBookRotation += 0.02F;
            blockEntity.nextPageTurningSpeed -= 0.1F;
        }

        while(blockEntity.bookRotation >= 3.1415927F) {
            blockEntity.bookRotation -= 6.2831855F;
        }

        while(blockEntity.bookRotation < -3.1415927F) {
            blockEntity.bookRotation += 6.2831855F;
        }

        while(blockEntity.targetBookRotation >= 3.1415927F) {
            blockEntity.targetBookRotation -= 6.2831855F;
        }

        while(blockEntity.targetBookRotation < -3.1415927F) {
            blockEntity.targetBookRotation += 6.2831855F;
        }

        float g;
        for(g = blockEntity.targetBookRotation - blockEntity.bookRotation; g >= 3.1415927F; g -= 6.2831855F) {
        }

        while(g < -3.1415927F) {
            g += 6.2831855F;
        }

        blockEntity.bookRotation += g * 0.4F;
        blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
        ++blockEntity.ticks;
        blockEntity.pageAngle = blockEntity.nextPageAngle;
        float h = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4F;
        float i = 0.2F;
        h = MathHelper.clamp(h, -0.2F, 0.2F);
        blockEntity.flipTurn += (h - blockEntity.flipTurn) * 0.9F;
        blockEntity.nextPageAngle += blockEntity.flipTurn;
    }



}
