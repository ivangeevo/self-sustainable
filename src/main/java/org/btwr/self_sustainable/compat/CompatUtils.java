package org.btwr.self_sustainable.compat;

import net.fabricmc.loader.api.FabricLoader;

public class CompatUtils {

    public static boolean isModLoaded(String name) {
        return FabricLoader.getInstance().isModLoaded(name);
    }
}
