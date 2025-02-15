package net.ivangeevo.self_sustainable.mixin.block;

import net.ivangeevo.self_sustainable.util.ExtinguishableTorch;
import net.minecraft.block.AbstractTorchBlock;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.TorchBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TorchBlock.class)
public abstract class TorchBlockMixin extends AbstractTorchBlock implements BlockEntityProvider, ExtinguishableTorch {

    protected TorchBlockMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isEverlastingTorch() {
        return true;
    }
}
