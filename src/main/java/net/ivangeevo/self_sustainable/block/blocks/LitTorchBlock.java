package net.ivangeevo.self_sustainable.block.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.particle.DefaultParticleType;

public class LitTorchBlock extends TorchBlock {
    private final Block unlitBlock;

    public LitTorchBlock(Settings settings, DefaultParticleType particleType, Block unlitBlock)
    {
        super(settings, particleType);
        this.unlitBlock = unlitBlock;
    }

    public Block getUnlitBlock() {
        return unlitBlock;
    }
}