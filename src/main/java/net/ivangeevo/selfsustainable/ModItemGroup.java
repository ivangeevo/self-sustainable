package net.ivangeevo.selfsustainable;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final ItemGroup SS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(SelfSustainableMod.MOD_ID, "ss_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ss_group"))
                    .icon(() -> new ItemStack(ModItems.SS_GROUP)).entries((displayContext, entries) -> {
                        /**
                        entries.add(Mod.CREEPER_OYSTERS);
                        entries.add(BTWR_Items.CLUB_BONE);
                        entries.add(BTWR_Items.CLUB_WOOD);
                        entries.add(BTWR_Items.DIAMOND_INGOT);
                        entries.add(BTWR_Items.DIAMOND_SHEARS);
                         **/


                    }).build());


}
