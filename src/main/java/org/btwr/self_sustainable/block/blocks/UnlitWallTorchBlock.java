package org.btwr.self_sustainable.block.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.btwr.self_sustainable.tag.ModTags;

/** Custom torch block for the unlit variant of the vanilla wall torches. **/
public class UnlitWallTorchBlock extends WallTorchBlock {

    public UnlitWallTorchBlock(SimpleParticleType particle, Settings settings) {
        super(particle, settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isIn(ModTags.Items.DIRECT_IGNITERS)) {
            if (!world.isClient) {
                this.btwr$setOnFireDirectly(world, pos);
            }
            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        // Override to remove particles for unlit torch
    }

    @Override
    public boolean btwr$getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return true;
    }

    @Override
    public boolean btwr$setOnFireDirectly(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        if (!world.hasRain(pos)) {
            BlockState newState = null;

            if (!world.hasRain(pos)) {
                if (state.isOf(ModBlocks.WALL_TORCH_UNLIT)) {
                    newState = Blocks.WALL_TORCH.getDefaultState().with(FACING, state.get(FACING));
                }
                else if (state.isOf(ModBlocks.SOUL_WALL_TORCH_UNLIT)) {
                    newState = Blocks.SOUL_WALL_TORCH.getDefaultState().with(FACING, state.get(FACING));
                }

                if (!world.isClient) {
                    if (newState != null) {
                        world.setBlockState(pos, newState);
                    }
                }

                world.playSound(
                        null,
                        BlockPos.ofFloored(pos.toCenterPos()),
                        ModSoundEvents.TORCH_IGNITE, SoundCategory.BLOCKS,
                        0.2F + world.random.nextFloat() * 0.1F,
                        world.random.nextFloat() * 0.25F + 1.25F
                );
            }
            else {
                float fizzPitch = 1F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F;
                world.playSound(
                        null, pos, ModSoundEvents.CAMPFIRE_EXTINGUISH,
                        SoundCategory.BLOCKS, 0.1F, fizzPitch
                );
            }
        }

        return true;
    }


}