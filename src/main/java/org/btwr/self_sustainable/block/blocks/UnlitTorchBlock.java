package org.btwr.self_sustainable.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.tag.ModTags;

/** Custom torch block for the unlit variant of the vanilla torches. **/
public class UnlitTorchBlock extends TorchBlock {

    public UnlitTorchBlock(SimpleParticleType particle, Settings settings) {
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
        // Override to remove particles for unlit torches
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
                if (state.isOf(ModBlocks.TORCH_UNLIT)) {
                    newState = Blocks.TORCH.getDefaultState();
                }
                else if (state.isOf(ModBlocks.SOUL_TORCH_UNLIT)) {
                    newState = Blocks.SOUL_TORCH.getDefaultState();
                }

                if (!world.isClient) {
                    if (newState != null) {
                        world.setBlockState(pos, newState);
                    }
                }

                IgnitableBlock.playLitFX(world, pos);
            }
            else {
                IgnitableBlock.playExtinguishSound(world, pos, true);
            }
        }

        return true;
    }

    @Override
    public int btwr$getChanceOfFireSpreadingDirectlyTo(WorldAccess blockAccess, BlockPos pos) {
        return 60; // same chance as leaves and other highly flammable objects
    }

}