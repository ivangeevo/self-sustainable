package ivangeevo.selfsustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.function.BiConsumer;

public class BTWRLootTableGenerator extends FabricBlockLootTableProvider {


    protected BTWRLootTableGenerator(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void generateBlockLootTables() {

    }

    @Override
    public LootContextType getLootContextType() {
        return null;
    }

    @Override
    public FabricDataGenerator getFabricDataGenerator() {
        return null;
    }

    @Override
    public void accept(BiConsumer<Identifier, LootTable.Builder> identifierBuilderBiConsumer) {

    }

    @Override
    public void run(DataWriter writer) throws IOException {

    }

    @Override
    public String getName() {
        return null;
    }
}
