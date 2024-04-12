package net.ivangeevo.self_sustainable.tag;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> MODDED_TORCHES = createTag("modded_torches");

        public static final TagKey<Block> LOOSE_BLOCKS = createTag("loose_blocks");


        private static TagKey<Block> createTag (String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> OVEN_COOKABLE = createTag("oven_cookable");
        public static final TagKey<Item> CAMPFIRE_IGNITER_ITEMS = createTag("campfire_igniter_items");

        public static final TagKey<Item> LOW_VALUE_FUELS = createTag("low_value_fuels");
        public static final TagKey<Item> MID_VALUE_FUELS = createTag("mid_value_fuels");
        public static final TagKey<Item> HIGH_VALUE_FUELS = createTag("high_value_fuels");
        public static final TagKey<Item> VERY_HIGH_VALUE_FUELS = createTag("very_high_value_fuels");
        public static final TagKey<Item> PRIMITIVE_FIRESTARTERS = createTag("primitive_firestarters");

        public static final TagKey<Item> SPIT_CAMPFIRE_ITEMS = createTag("spit_campfire_items");
        public static final TagKey<Item> WOOL_ITEMS = createTag("wool_items");
        public static final TagKey<Item> WOOL_KNIT_ITEMS = createTag("wool_knit_items");



        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }
}
