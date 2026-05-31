package org.btwr.self_sustainable.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;

public class ModSoundEvents {
    public static final SoundEvent PRIMITIVE_FIRESTARTER_USE = register("primitive_firestarter_use");
    public static final SoundEvent CAMPFIRE_IGNITE = register("campfire_ignite");
    public static final SoundEvent CAMPFIRE_BURNING = register("campfire_burning");
    public static final SoundEvent CAMPFIRE_EXTINGUISH = register("campfire_extinguish");
    public static final SoundEvent OVEN_IGNITE = register("oven_ignite");
    public static final SoundEvent OVEN_INSERT_FUEL = register("oven_insert_fuel");
    public static final SoundEvent OVEN_INSERT_FUEL_ACTIVE = register("oven_insert_fuel_active");
    public static final SoundEvent OVEN_MORTAR = register("oven_mortar");
    public static final SoundEvent TORCH_IGNITE = register("torch_ignite");
    public static final SoundEvent TORCH_SMOULDER = register("torch_smoulder");
    public static final SoundEvent TORCH_EXTINGUISH = register("torch_extinguish");
    public static final SoundEvent KNITTING = register("knitting");
    public static final SoundEvent KNITTING_FINISH = register("knitting_finish");
    public static final SoundEvent WICKER_WEAVING = register("wicker_weaving");
    public static final SoundEvent WICKER_WEAVING_FINISH = register("wicker_weaving_finish");
    public static final SoundEvent WICKER_BASKET_OPEN = register("wicker_basket_open");
    public static final SoundEvent WICKER_BASKET_CLOSE = register("wicker_basket_close");
    public static final SoundEvent WICKER_BASKET_INSERT_ITEM = register("wicker_basket_insert_item");
    public static final SoundEvent WICKER_BASKET_TAKE_OUT_ITEM = register("wicker_basket_take_out_item");
    public static final SoundEvent HAMPER_OPEN = register("hamper_open");
    public static final SoundEvent HAMPER_CLOSE = register("hamper_close");

    private static SoundEvent register(String name) {
        Identifier id = IdUtils.ofSS(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {}

}