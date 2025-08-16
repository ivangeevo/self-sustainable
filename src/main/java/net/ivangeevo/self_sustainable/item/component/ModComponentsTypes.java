package net.ivangeevo.self_sustainable.item.component;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.component.ComponentType;
import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModComponentsTypes {

    public static final ComponentType<Long> LAST_USE = ComponentType.<Long>builder()
            // Use xmap to box long to Long
            .codec(Codec.LONG.xmap(Long::valueOf, longValue -> longValue))
            .build();

    public static final ComponentType<Float> ACCUMULATED_CHANCE = ComponentType.<Float>builder()
            // box float to Float
            .codec(Codec.FLOAT.xmap(Float::valueOf, Float::floatValue))
            .build();

    public static final ComponentType<TorchFuelComponent> TORCH_FUEL = ComponentType.<TorchFuelComponent>builder()
            .codec(TorchFuelComponent.CODEC)
            .build();

    public static final ComponentType<ProgressiveCraftingComponent> PROGRESSIVE_CRAFTING = ComponentType.<ProgressiveCraftingComponent>builder()
            .codec(ProgressiveCraftingComponent.CODEC)
            .build();

    // Register method, to be called in the mod initialization
    public static void register() {
        registerDataComponent(LAST_USE, "last_use");
        registerDataComponent(ACCUMULATED_CHANCE, "accumulated_chance");
        registerDataComponent(TORCH_FUEL, "torch_fuel");
        registerDataComponent(PROGRESSIVE_CRAFTING, "progressive_crafting");
    }

    private static void registerDataComponent(ComponentType<?> componentType, String stringName) {
        register(Registries.DATA_COMPONENT_TYPE, componentType, stringName);
    }

    private static void register(Registry<ComponentType<?>> registryType, ComponentType<?> componentType, String stringName)
    {
        Registry.register(registryType, SelfSustainableMod.MOD_ID + ":" + stringName, componentType);
    }


}
