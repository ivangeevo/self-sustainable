package org.btwr.self_sustainable.mixin.added;

import org.btwr.self_sustainable.world.interfaces.added.DifficultyAdded;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Difficulty.class)
public abstract class DifficultyAddedMixin implements DifficultyAdded {

    @Override
    public float btwr$getHungerIntensiveActionCostMultiplier() {
        return 1;
    }

}