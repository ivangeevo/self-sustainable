package org.btwr.self_sustainable.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.Arrays;
import java.util.Comparator;

public class DyeColorUtils {

    public static DyeColor getClosestDyeColor(int rgb) {
        return Arrays.stream(DyeColor.values())
                .min(Comparator.comparingInt(c -> colorDistance(c.getFireworkColor(), rgb)))
                .orElse(DyeColor.WHITE);
    }

    public static int colorDistance(int rgb1, int rgb2) {
        int dr = ((rgb1 >> 16) & 0xFF) - ((rgb2 >> 16) & 0xFF);
        int dg = ((rgb1 >> 8) & 0xFF) - ((rgb2 >> 8) & 0xFF);
        int db = (rgb1 & 0xFF) - (rgb2 & 0xFF);
        return dr * dr + dg * dg + db * db;
    }

    private static ItemStack stackWithColor(Item item, int color) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(color, true));
        return stack;
    }
}
