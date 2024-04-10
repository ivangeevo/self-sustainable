package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.ivangeevo.self_sustainable.block.ModBlocks;
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
                    .icon(() -> new ItemStack(ModItems.SS_GROUP)).entries((displayContext, entries) ->
                    {
                        entries.add(ModItems.KNITTING_NEEDLES);
                        entries.add(ModItems.FIRESTARTER_STICKS);
                        entries.add(ModItems.FIRESTARTER_BOW);

                        entries.add(ModBlocks.OVEN_BRICK);
                        entries.add(ModBlocks.CRUDE_TORCH);
                        entries.add(ModBlocks.TORCH);




                    })
                    .build());

    public static void registerItemGroups()
    {
        SelfSustainableMod.LOGGER.info("Registering Item Groups for " + SelfSustainableMod.MOD_ID);

    }


}
