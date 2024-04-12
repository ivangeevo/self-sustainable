package net.ivangeevo.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class SelfSustainableModelGenerator extends FabricModelProvider {


    public SelfSustainableModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    // @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        //itemModelGenerator.register(SelfSustainable.WOOL_HELM, Models.GENERATED);
        //itemModelGenerator.register(SelfSustainable.WOOL_CHEST, Models.GENERATED);
        //itemModelGenerator.register(SelfSustainable.WOOL_LEGGINGS, Models.GENERATED);
        //itemModelGenerator.register(SelfSustainable.WOOL_BOOTS, Models.GENERATED);

    }
}
