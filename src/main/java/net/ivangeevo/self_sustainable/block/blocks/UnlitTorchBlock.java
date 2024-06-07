package net.ivangeevo.self_sustainable.block.blocks;

import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class UnlitTorchBlock extends TorchBlock implements Ignitable
{
    public UnlitTorchBlock(Settings settings, ParticleEffect particle) {
        super(settings, particle);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getMainHandStack();

        Block torchBlock = Blocks.TORCH;

        BlockState blockState  = torchBlock.getStateWithProperties(state);

        if (heldStack.isIn(ModTags.Items.DIRECT_IGNITERS))
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

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return true;
    }
}
