package net.ivangeevo.self_sustainable.block.blocks;

import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class UnlitWallTorchBlock extends WallTorchBlock
{
    public UnlitWallTorchBlock(Settings settings, ParticleEffect particle) {
        super(settings, particle);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getMainHandStack();

        Block torchBlock = Blocks.WALL_TORCH;

        BlockState blockState  = torchBlock.getStateWithProperties(state);

        if (heldStack.isIn(ModTags.Items.DIRECT_IGNITERS) )
        {
            if ( !world.isClient )
            {
                world.setBlockState(pos, blockState);
            }
            Ignitable.playLitFX(world, pos);
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    // overriding to remove particles
    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
    }
}
