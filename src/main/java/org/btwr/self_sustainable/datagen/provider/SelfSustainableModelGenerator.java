package org.btwr.self_sustainable.datagen.provider;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
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
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        for (DyeColor color : DyeColor.values()) {
            itemModelGenerator.register(
                    ModItems.WOOL_KNITS.get(color),
                    new Model(
                            Optional.of(Identifier.of(SelfSustainableMod.MOD_ID, "item/wool_knit")),
                            Optional.empty()
                    )
            );

            itemModelGenerator.register(
                    ModItems.WOOLS.get(color),
                    new Model(
                            Optional.of(Identifier.of(SelfSustainableMod.MOD_ID, "item/wool")),
                            Optional.empty()
                    )
            );
        }
    }

}