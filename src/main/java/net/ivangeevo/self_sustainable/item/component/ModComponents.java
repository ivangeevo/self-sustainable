package net.ivangeevo.self_sustainable.item.component;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.component.ComponentType;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    // Define your custom components with proper boxing
    public static final ComponentType<Long> LAST_USE_COMPONENT = ComponentType.<Long>builder()
            // Use xmap to box long to Long
            .codec(Codec.LONG.xmap(Long::valueOf, longValue -> longValue))
            .build();

    public static final ComponentType<Float> ACCUMULATED_CHANCE_COMPONENT = ComponentType.<Float>builder()
            // Use xmap to box float to Float
            .codec(Codec.FLOAT.xmap(Float::valueOf, Float::floatValue))
            .build();



    // Register method, to be called in the mod initialization
    public static void registerComponents() {
        register(Registries.DATA_COMPONENT_TYPE, LAST_USE_COMPONENT, "last_use_component");
        register(Registries.DATA_COMPONENT_TYPE, ACCUMULATED_CHANCE_COMPONENT, "accumulated_chance_component");
    }

    private static void register(Registry<ComponentType<?>> registryType, ComponentType<?> componentType, String stringName )
    {
        Registry.register(registryType, SelfSustainableMod.MOD_ID + ":" + stringName, componentType);
    }


}
