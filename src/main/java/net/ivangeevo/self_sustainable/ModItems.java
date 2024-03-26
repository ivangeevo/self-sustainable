package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item SS_GROUP = registerItem( "ss_group", new Item(new FabricItemSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SelfSustainableMod.LOGGER.info("Registering Mod Items for " + SelfSustainableMod.MOD_ID);

    }
}
