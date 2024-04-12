package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.LitLanternBlock;
import net.ivangeevo.self_sustainable.block.blocks.LitTorchBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.tick.OrderedTick;
import net.minecraft.world.tick.TickPriority;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {

    /**
    @Inject(method = "scheduledTick", at = @At("HEAD"))
    private void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        updateBlockState(state, world, pos, random);
    }

    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    private void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (state.isIn(BlockTags.CAMPFIRES)) {
            handleCampfireBlock(state, world, pos, random);
        } else if (state.isIn(BlockTags.CANDLES) || state.isIn(BlockTags.CANDLE_CAKES)) {
            handleCandleBlock(state, world, pos, random);
        } else if (state.isOf(Blocks.JACK_O_LANTERN)) {
            handleLanternBlock(state, world, pos, random, SelfSustainableMod.getInstance().settings.jackOLanternExtinguishInRainChance, SelfSustainableMod.getInstance().settings.jackOLanternBurnDuration);
        } else if (state.isOf(Blocks.LANTERN) || state.getBlock() instanceof LitLanternBlock) {
            handleLanternBlock(state, world, pos, random, SelfSustainableMod.getInstance().settings.jackOLanternExtinguishInRainChance, SelfSustainableMod.getInstance().settings.lanternBurnDuration);
        } else if (state.isOf(Blocks.TORCH) || state.isOf(Blocks.WALL_TORCH) || state.getBlock() instanceof TorchBlock || state.getBlock() instanceof WallTorchBlock) {
            handleTorchBlock(state, world, pos, random);
        }
        ci.cancel();
    }

    private void updateBlockState(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        Block block = state.getBlock();

        if (block instanceof AbstractCandleBlock)
        {
            AbstractCandleBlock.extinguish(null, state, world, pos);
        }
        else if (block instanceof CarvedPumpkinBlock)
        {
            world.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, state.get(CarvedPumpkinBlock.FACING)), Block.NOTIFY_ALL);
        }
        else if (block instanceof LanternBlock)
        {
            world.setBlockState(pos, Blocks.LANTERN.getDefaultState().with(LanternBlock.HANGING, state.get(LanternBlock.HANGING)).with(LanternBlock.WATERLOGGED, state.get(LanternBlock.WATERLOGGED)), Block.NOTIFY_ALL);
        }
        else if (block instanceof TorchBlock)
        {
            world.setBlockState(pos, Blocks.TORCH.getDefaultState(), Block.NOTIFY_ALL);
        }
        else if (block instanceof WallTorchBlock)
        {
            world.setBlockState(pos, Blocks.WALL_TORCH.getDefaultState().with(WallTorchBlock.FACING, state.get(WallTorchBlock.FACING)), Block.NOTIFY_ALL);
        }
        else if (block instanceof LitTorchBlock)
        {
            world.setBlockState(pos, ((LitTorchBlock) block).getUnlitBlock().getDefaultState(), Block.NOTIFY_ALL);
        }
        else if (block instanceof LitWallTorchBlock)
        {
            world.setBlockState(pos, ((LitWallTorchBlock) block).getUnlitBlock().getDefaultState().with(WallTorchBlock.FACING, state.get(WallTorchBlock.FACING)), Block.NOTIFY_ALL);
        }

        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.0625F, random.nextFloat() * 0.5F + 0.125F);
    }

    private void handleCampfireBlock(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        final BlockEntity blockEntity = world.getBlockEntity(pos);

        if (world.hasRain(pos.up()) && CampfireBlock.isLitCampfire(state) && blockEntity instanceof CampfireBlockEntity && random.nextFloat() < SelfSustainableMod.getInstance().settings.campfireExtinguishInRainChance && world.setBlockState(pos, state.with(CampfireBlock.LIT, false))) {
            ((CampfireBlockEntity) blockEntity).setLitTime(0);
            CampfireBlock.extinguish(null, world, pos, state);
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    private void handleCandleBlock(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (AbstractCandleBlock.isLitCandle(state) && ((world.hasRain(pos.up()) && random.nextFloat() < SelfSustainableMod.getInstance().settings.candleExtinguishInRainChance) || (state.contains(CandleBlock.WATERLOGGED) && state.get(CandleBlock.WATERLOGGED)))) {
            OrderedTick<Block> orderedTick = new OrderedTick<Block>(state.getBlock(), pos, )
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2, TickPriority.NORMAL);
        }
        else if (AbstractCandleBlock.isLitCandle(state) && SelfSustainableMod.getInstance().settings.extinguishCandles)
        {
            scheduleBlockTick(world, pos, SelfSustainableMod.getInstance().settings.candleBurnDuration, state.getBlock());
        }
    }

    private void handleLanternBlock(BlockState state, ServerWorld world, BlockPos pos, Random random, float extinguishChance, int burnDuration)
    {
        if ((world.hasRain(pos) && random.nextFloat() < extinguishChance) || Boolean.TRUE.equals(state.get(LanternBlock.WATERLOGGED)))
        {
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2, TickPriority.NORMAL);
        }
        else if (SelfSustainableMod.getInstance().settings.extinguishJackOLanterns)
        {
            scheduleBlockTick(world, pos, burnDuration, state.getBlock());
        }
    }

    private void handleTorchBlock(BlockState state, ServerWorld world, BlockPos pos, Random random)
    {
        if (world.hasRain(pos) && random.nextFloat() < SelfSustainableMod.getInstance().settings.torchExtinguishInRainChance)
        {
            world.getBlockTickScheduler().schedule(pos, state.getBlock(), 2, TickPriority.NORMAL);
        }
        else if (SelfSustainableMod.getInstance().settings.extinguishTorches)
        {
            scheduleBlockTick(world, pos, SelfSustainableMod.getInstance().settings.torchBurnDuration, state.getBlock());
        }
    }

    private void scheduleBlockTick(ServerWorld world, BlockPos pos, int delay, Block block)
    {
        WorldTickScheduler<Block> scheduler = world.getBlockTickScheduler();

        if (!scheduler.isQueued(pos, block) && !scheduler.isTicking(pos, block))
        {
            scheduler.schedule(pos, block, delay);
        }
    }
    **/
}
