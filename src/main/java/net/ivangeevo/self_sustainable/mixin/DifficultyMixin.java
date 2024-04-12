package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.world.interfaces.DifficultyAdded;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Difficulty.class)
public abstract class DifficultyMixin implements DifficultyAdded
{
    @Override
    public float getHungerIntensiveActionCostMultiplier() {
        return 1;
    }
}
