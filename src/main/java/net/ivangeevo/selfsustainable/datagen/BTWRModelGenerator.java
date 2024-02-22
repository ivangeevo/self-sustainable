package net.ivangeevo.selfsustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;

public class BTWRModelGenerator extends FabricModelProvider {


    public BTWRModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    // @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

        //itemModelGenerator.register(BTWR_Items.WOOL_HELM, Models.GENERATED);
        //itemModelGenerator.register(BTWR_Items.WOOL_CHEST, Models.GENERATED);
        //itemModelGenerator.register(BTWR_Items.WOOL_LEGGINGS, Models.GENERATED);
        //itemModelGenerator.register(BTWR_Items.WOOL_BOOTS, Models.GENERATED);

    }
}
