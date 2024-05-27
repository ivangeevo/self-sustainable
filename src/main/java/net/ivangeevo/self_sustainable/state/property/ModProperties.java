package net.ivangeevo.self_sustainable.state.property;

import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;

public class ModProperties {

    public static final IntProperty FUEL_LEVEL = IntProperty.of("fuel_level", 0, 8);
    public static final IntProperty FIRE_SIZE = IntProperty.of("fire_size", 0, 4);
    public static final BooleanProperty EMBERS = BooleanProperty.of("embers");
    public static final BooleanProperty HAS_SPIT = BooleanProperty.of("has_spit");




}
