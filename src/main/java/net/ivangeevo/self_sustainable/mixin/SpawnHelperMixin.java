package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpawnHelper.class)
public abstract class SpawnHelperMixin
{

    // Removes the random respawning of passive mobs
    @Inject(method = "spawnEntitiesInChunk*", at = @At("HEAD"), cancellable = true)
    private static void cancelPassiveMobRespawn(SpawnGroup group, ServerWorld world, WorldChunk chunk, SpawnHelper.Checker checker, SpawnHelper.Runner runner, CallbackInfo ci) {
        if (group.isPeaceful()) {
            ci.cancel();
        }
    }
}
