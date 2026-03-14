package org.btwr.self_sustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.btwr.self_sustainable.datagen.provider.*;

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
    }

}