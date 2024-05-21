package net.ivangeevo.self_sustainable.block.blocks;

import net.ivangeevo.self_sustainable.block.entity.CrudeTorchBlockEntity;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


public class CrudeTorchBlock extends ModTorchBlock
{

    public CrudeTorchBlock(Settings settings, ParticleEffect particle) {
        super(settings, particle);
    }

    /**
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrudeTorchBlockEntity(pos, state);
    }



    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient)
        {
            return CrudeTorchBlock.checkType(type, ModBlockEntities.CRUDE_TORCH, CrudeTorchBlockEntity::clientTick);
        }
        else
        {
            return CrudeTorchBlock.checkType(type, ModBlockEntities.CRUDE_TORCH, CrudeTorchBlockEntity::serverTick);
        }
    }
    **/
}

