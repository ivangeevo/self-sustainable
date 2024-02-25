package net.ivangeevo.selfsustainable.state.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModProperties {

    public static final IntProperty FUEL_LEVEL = IntProperty.of("fuel_level", 0, 9);
    public static final BooleanProperty HAS_FUEL = BooleanProperty.of("has_fuel");


}
