package net.ivangeevo.self_sustainable.mixin.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.CampfireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlock$AbstractBlockStateMixin
{

    // TODO: Fix this so it's setting the campfire light level here instead of in the BlocksMixin
    //@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractBlock$AbstractBlockState;asBlockState()Lnet/minecraft/block/BlockState;"), index = 1)
    private int injected() {
        return setLightLevel((AbstractBlock.AbstractBlockState)(Object)this);
    }


    @Unique
    private int setLightLevel(AbstractBlock.AbstractBlockState state)
    {
        // Check if the block is CampfireBlock or its subclass
        if (state.getBlock() instanceof CampfireBlock)
        {
            switch (state.get(FIRE_LEVEL))
            {
                case 0: return 0;
                case 1: return 8;
                case 2: return 11;
                case 3: return 14;
            }
        }

        return 0;
    }
}
