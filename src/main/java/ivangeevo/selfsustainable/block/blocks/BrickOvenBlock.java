package ivangeevo.selfsustainable.block.blocks;

import ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import ivangeevo.selfsustainable.entity.ModEntities;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BrickOvenBlock extends AbstractOvenBlock {
    public BrickOvenBlock(Settings settings) {
        super(settings);
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return super.getRenderType(state);
    }

    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, ModEntities.Blocks.OVEN_BRICK, BrickOvenBlockEntity::tick);
    }

    protected void openScreen(World world, BlockPos pos, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrickOvenBlockEntity) {
            player.openHandledScreen((NamedScreenHandlerFactory) blockEntity);
            player.incrementStat(Stats.INTERACT_WITH_FURNACE);
        }

    }

    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        // Check if the oven is active (burning) and has an item in slot 0
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (state.get(LIT) && blockEntity instanceof BrickOvenBlockEntity) {
            ItemStack cookingStack = ((BrickOvenBlockEntity) blockEntity).getStack(0);
            if (!cookingStack.isEmpty()) {
                double d = (double) pos.getX() + 0.5;
                double e = pos.getY();
                double f = (double) pos.getZ() + 0.5;

                // Smoke particle
                if (random.nextDouble() < 0.1) {
                    world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
                }

                Direction direction = state.get(FACING);
                Direction.Axis axis = direction.getAxis();
                double g = 0.52;
                double h = random.nextDouble() * 0.6 - 0.3;
                double i = axis == Direction.Axis.X ? (double) direction.getOffsetX() * 0.52 : h;
                double j = random.nextDouble() * 9.0 / 16.0;
                double k = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * 0.52 : h;
                world.addParticle(ParticleTypes.CLOUD, d + i, e + j, f + k, 0.0, 0.0, 0.0);

                // White rising particles
                if (random.nextDouble() < 0.1) {
                    world.playSound(d, e, f, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
                }

                double particleX = random.nextDouble() * 0.3 - 0.15 + (axis == Direction.Axis.X ? direction.getOffsetX() * 0.26 : 0);
                double particleY = random.nextDouble() * 0.15; // Adjusted size and direction
                double particleZ = random.nextDouble() * 0.3 - 0.15 + (axis == Direction.Axis.Z ? direction.getOffsetZ() * 0.26 : 0);

                world.addParticle(ParticleTypes.CLOUD, d + particleX, e + j, f + particleZ, 0.0, particleY, 0.0);
            }
        }
    }
    // ... existing code ...
}
