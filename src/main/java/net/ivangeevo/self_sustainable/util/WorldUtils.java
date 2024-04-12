package net.ivangeevo.self_sustainable.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelProperties;

public class WorldUtils {

    private static MinecraftServer serverInstance;

    public static void init()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            serverInstance = server;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            serverInstance = null;
        });
    }

    public static long getOverworldTimeServerOnly()
    {
        if (serverInstance != null)
        {
            LevelProperties worldProperties = (LevelProperties) serverInstance.getOverworld().getLevelProperties();
            return worldProperties.getTime();
        }
        return 0;
    }
}
