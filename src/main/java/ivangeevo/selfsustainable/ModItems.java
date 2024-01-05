package ivangeevo.selfsustainable;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item GROUP_BTWR = registerItem( "group_btwr", new Item(new FabricItemSettings()));






    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SelfSustainableMod.LOGGER.info("Registering Mod Items for " + SelfSustainableMod.MOD_ID);

    }
}
