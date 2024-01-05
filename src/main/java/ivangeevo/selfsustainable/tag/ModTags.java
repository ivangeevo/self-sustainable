package ivangeevo.selfsustainable.tag;

import ivangeevo.selfsustainable.SelfSustainableMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> MODDED_TORCHES = createTag("modded_torches");

        private static TagKey<Block> createTag (String name) {
            return TagKey.of(Registry.BLOCK_KEY, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }

    public static class Items {


        private static TagKey<Item> createTag (String name) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier(SelfSustainableMod.MOD_ID, name));
        }
    }
}
