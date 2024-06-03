package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
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

import java.util.function.ToIntFunction;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;
import static net.minecraft.state.property.Properties.LIT;

@Mixin(Blocks.class)
public abstract class BlocksMixin {

    /**
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
    **/

    @Inject(method = "createLightLevelFromLitBlockState", at = @At("HEAD"), cancellable = true)
    private static void injectedLightLevelCampfire(int litLevel, CallbackInfoReturnable<ToIntFunction<BlockState>> cir)
    {
        cir.setReturnValue(state -> {
            // Check if the block is CampfireBlock or its subclass
            if (state.getBlock() instanceof CampfireBlock) {
                // Modify the return value for CampfireBlock
                return state.get(FIRE_LEVEL) != 0 ? litLevel : 0;
            }
            // Return default value for other blocks
            return 0;
        });
    }


}
