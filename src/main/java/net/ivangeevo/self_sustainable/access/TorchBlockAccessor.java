package net.ivangeevo.self_sustainable.access;

import net.minecraft.block.BlockState;
import net.minecraft.state.property.BooleanProperty;

// The duck interface being implemented onto `MyClass`
public interface TorchBlockAccessor
{
    BooleanProperty accessLit();

}