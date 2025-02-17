package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.ivangeevo.self_sustainable.datagen.*;


public class SelfSustainableDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(SelfSustainableRecipeProvider::new);
        pack.addProvider(SelfSustainableBlockTagProvider::new);
        pack.addProvider(SelfSustainableItemTagProvider::new);
        pack.addProvider(SelfSustainableLootTableProvider::new);
        pack.addProvider(SelfSustainableLangProvider::new);
    }
}
