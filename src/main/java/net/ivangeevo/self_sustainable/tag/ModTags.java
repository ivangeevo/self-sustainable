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


        /** Blocks that can be instantly ignited on use(right click). **/
        public static final TagKey<Block> DIRECTLY_IGNITABLE = createTag("directly_ignitable");



        private static TagKey<Block> createTag (String name) {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> OVEN_COOKABLE = createTag("oven_cookable");
        public static final TagKey<Item> CAN_BE_SET_ON_FIRE_ON_USE = createTag("can_be_set_on_fire_on_use");
        public static final TagKey<Item> UNLIT_TORCHES = createTag("can_be_set_on_fire_on_use");


        /** Items that can instantly start a fire on use(right click). **/
        public static final TagKey<Item> DIRECT_IGNITERS = createTag("direct_igniters");

        public static final TagKey<Item> PRIMITIVE_FIRESTARTERS = createTag("primitive_firestarters");
        public static final TagKey<Item> WOOL_ITEMS = createTag("wool_items");
        public static final TagKey<Item> WOOL_KNIT_ITEMS = createTag("wool_knit_items");



        private static TagKey<Item> createTag (String name) {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }
}
