package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final ItemGroup SS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(SelfSustainableMod.MOD_ID, "ss_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.ss_group"))
                    .icon(() -> new ItemStack(ModItems.SS_GROUP)).entries((displayContext, entries) ->
                    {
                        // Items
                        entries.add(ModItems.FIRESTARTER_STICKS);
                        entries.add(ModItems.FIRESTARTER_BOW);
                        entries.add(ModItems.BRICK_UNFIRED);

                        entries.add(ModItems.CRUDE_TORCH_UNLIT);
                        //entries.add(ModItems.CRUDE_TORCH_LIT);
                        //entries.add(ModItems.CRUDE_TORCH_SMOULDER);
                        //entries.add(ModItems.CRUDE_TORCH_BURNED_OUT);

                        entries.add(ModItems.TORCH_UNLIT);

                        // Blocks

                        // TO ADD:
                        //entries.add(ModBlocks.SMOKER_BRICK);
                        //entries.add(ModBlocks.OVEN_BRICK);
                        //entries.add(ModItems.WICKER_PANE);
                        //entries.add(ModItems.KNITTING_NEEDLES);

                    }).build());

    public static void registerItemGroups()
    {
        SelfSustainableMod.LOGGER.info("Registering Item Groups for " + SelfSustainableMod.MOD_ID);

    }


}
