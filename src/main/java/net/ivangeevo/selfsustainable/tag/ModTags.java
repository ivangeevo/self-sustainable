package net.ivangeevo.selfsustainable.tag;

import net.ivangeevo.selfsustainable.SelfSustainableMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> MODDED_TORCHES = createTag("modded_torches");

        private static TagKey<Block> createTag (String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> OVEN_COOKABLE = createTag("oven_cookable");


        public static final TagKey<Item> LOW_VALUE_FUELS = createTag("low_value_fuels");
        public static final TagKey<Item> MID_VALUE_FUELS = createTag("mid_value_fuels");
        public static final TagKey<Item> HIGH_VALUE_FUELS = createTag("high_value_fuels");
        public static final TagKey<Item> VERY_HIGH_VALUE_FUELS = createTag("very_high_value_fuels");


        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }
}
