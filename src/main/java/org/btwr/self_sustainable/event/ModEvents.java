package org.btwr.self_sustainable.event;

import org.btwr.self_sustainable.event.events.ModBlockBreakEvents;
import org.btwr.self_sustainable.event.events.ModLootTableEvents;
import org.btwr.self_sustainable.event.events.ModUseBlockEvents;

public class ModEvents {
    public static void register() {
        ModUseBlockEvents.register();
        ModLootTableEvents.register();
        ModBlockBreakEvents.register();
    }
}