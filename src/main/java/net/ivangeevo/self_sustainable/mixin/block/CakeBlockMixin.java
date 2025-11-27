package net.ivangeevo.self_sustainable.mixin.block;

import net.minecraft.block.CakeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin {

    @ModifyConstant(method = "tryEat", constant = @Constant(intValue = 2))
    private static int modifyCakeHungerGiven(int constant) {
        return 1;
    }

    @ModifyConstant(method = "tryEat", constant = @Constant(floatValue = 0.1f))
    private static float modifyCakeSaturationGiven(float constant) {
        return 0;
    }

}