package org.btwr.self_sustainable;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.item.ModItems;
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
                        entries.add(ModItems.SOUL_TORCH_UNLIT);

                        ModItems.WOOL_KNITS.forEach((dyeColor, item) -> {
                            entries.add(item);
                        });

                        entries.add(ModItems.WOOL_HELMET);
                        entries.add(ModItems.WOOL_CHESTPLATE);
                        entries.add(ModItems.WOOL_LEGGINGS);
                        entries.add(ModItems.WOOL_BOOTS);

                        // Blocks
                        entries.add(ModBlocks.OVEN_BRICK);


                        //entries.add(ModBlocks.WICKER_BASKET);
                        //entries.add(ModBlocks.HAMPER);

                        // TO ADD:
                        //entries.add(ModBlocks.SMOKER_BRICK);
                        //entries.add(ModItems.WICKER_PANE);
                        //entries.add(ModItems.KNITTING_NEEDLES);

                    }).build());

    public static void register() {
        SelfSustainableMod.LOGGER.info("Registering Item Groups for " + SelfSustainableMod.MOD_ID);
    }

}