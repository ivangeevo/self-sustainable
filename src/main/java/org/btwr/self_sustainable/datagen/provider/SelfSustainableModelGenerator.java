package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.Models;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.item.ModItems;

import java.util.Optional;

public class SelfSustainableModelGenerator extends FabricModelProvider {

    public SelfSustainableModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator generator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator generator) {
        generator.register(ModItems.EGG_SCRAMBLED_RAW, Models.GENERATED);
        generator.register(ModItems.MUSHROOM_OMELETTE_RAW, Models.GENERATED);
        generator.register(ModItems.EGG_SCRAMBLED_COOKED, Models.GENERATED);
        generator.register(ModItems.MUSHROOM_OMELETTE_COOKED, Models.GENERATED);
        generator.register(ModItems.SANDWICH, Models.GENERATED);
        generator.register(ModItems.HAM_AND_EGGS, Models.GENERATED);
        generator.register(ModItems.CHOWDER, Models.GENERATED);
        generator.register(ModItems.STEAK_AND_POTATOES, Models.GENERATED);
        generator.register(ModItems.RAW_KEBAB, Models.GENERATED);
        generator.register(ModItems.COOKED_KEBAB, Models.GENERATED);
        generator.register(ModItems.STEAK_DINNER, Models.GENERATED);
        generator.register(ModItems.PORK_DINNER, Models.GENERATED);
        generator.register(ModItems.WOLF_DINNER, Models.GENERATED);
        generator.register(ModItems.CHICKEN_SOUP, Models.GENERATED);
        generator.register(ModItems.HEARTY_STEW, Models.GENERATED);

        for (DyeColor color : DyeColor.values()) {
            generator.register(
                    ModItems.WOOL_KNITS.get(color),
                    new Model(
                            Optional.of(Identifier.of(SelfSustainableMod.MOD_ID, "item/wool_knit")),
                            Optional.empty()
                    )
            );

            generator.register(
                    ModItems.WOOLS.get(color),
                    new Model(
                            Optional.of(Identifier.of(SelfSustainableMod.MOD_ID, "item/wool")),
                            Optional.empty()
                    )
            );
        }
    }

}