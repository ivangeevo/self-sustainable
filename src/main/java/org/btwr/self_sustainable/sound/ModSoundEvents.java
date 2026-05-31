package org.btwr.self_sustainable.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.btwr.shared_library.util.utils.IdUtils;

public class ModSoundEvents {
    public static final SoundEvent OVEN_IGNITE = register("oven_ignite");
    public static final SoundEvent TORCH_IGNITE = register("torch_ignite");

    private static SoundEvent register(String name) {
        Identifier id = IdUtils.ofSS(name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {}

}