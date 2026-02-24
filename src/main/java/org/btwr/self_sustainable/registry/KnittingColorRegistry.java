package org.btwr.self_sustainable.registry;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.util.DyeColorUtils;

import java.util.HashMap;
import java.util.Map;

public class KnittingColorRegistry {
    private static final Map<Item, DyeColor> COLOR_MAP = new HashMap<>();

    public static void register(Item item, DyeColor color) {
        COLOR_MAP.put(item, color);
    }

    public static void unregister(Item item) {
        COLOR_MAP.remove(item);
    }

    public static DyeColor getColor(ItemStack stack) {
        // Check registry first
        DyeColor registered = COLOR_MAP.get(stack.getItem());
        if (registered != null) return registered;

        // Fall back to DYED_COLOR component
        DyedColorComponent dyed = stack.get(DataComponentTypes.DYED_COLOR);
        if (dyed != null) return DyeColorUtils.getClosestDyeColor(dyed.rgb());

        return null;
    }


}
