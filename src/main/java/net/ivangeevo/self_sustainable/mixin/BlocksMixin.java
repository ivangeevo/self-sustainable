package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.state.property.Properties.LIT;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    // TorchBlock's luminance lambda
    @Inject(method = "method_26136", at = @At("HEAD"), cancellable = true)
    private static void customTorchLuminance(BlockState state, CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(state.get(LIT) ? 14 : 0);
    }

    // WallTorchBlock's luminance lambda
    @Inject(method = "method_26152", at = @At("HEAD"), cancellable = true)
    private static void customWallTorchLuminance(BlockState state, CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(state.get(LIT) ? 14 : 0);
    }

}
