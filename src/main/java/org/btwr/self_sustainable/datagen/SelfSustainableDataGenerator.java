package org.btwr.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.datagen.provider.*;
import org.btwr.self_sustainable.datagen.provider.custom.DyeColoredRecipeProvider;
import org.btwr.self_sustainable.item.ModItems;

import java.util.List;
import java.util.Map;

public class SelfSustainableDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(SelfSustainableRecipeProvider::new);
        pack.addProvider(SelfSustainableBlockTagProvider::new);
        pack.addProvider(SelfSustainableItemTagProvider::new);
        pack.addProvider(SelfSustainableLootTableProvider::new);
        pack.addProvider(SelfSustainableLangProvider::new);
        pack.addProvider(SelfSustainableModelGenerator::new);

        pack.addProvider((FabricDataGenerator.Pack.Factory<DyeColoredRecipeProvider>) output -> new DyeColoredRecipeProvider(output, List.of(
                dyeColorRecipeEntry(
                        "wool_helmet", ModItems.WOOL_HELMET, ModItems.WOOL_KNITS, new String[]{"##"}
                ),
                dyeColorRecipeEntry(
                        "wool_chestplate", ModItems.WOOL_CHESTPLATE, ModItems.WOOL_KNITS, new String[]{"##", "##"}
                ),
                dyeColorRecipeEntry(
                        "wool_leggings", ModItems.WOOL_LEGGINGS, ModItems.WOOL_KNITS, new String[]{"# ", "##"}
                )
        )));
    }

    private DyeColoredRecipeProvider.Entry dyeColorRecipeEntry(
            String recipeBaseName, Item output, Map<DyeColor, Item> inputs, String[] pattern)
    {
        return new DyeColoredRecipeProvider.Entry(recipeBaseName, output, inputs, pattern);
    }

}