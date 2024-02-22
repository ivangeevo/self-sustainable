package net.ivangeevo.selfsustainable.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.function.BiConsumer;

public class BTWRLootTableGenerator extends FabricBlockLootTableProvider {


    protected BTWRLootTableGenerator(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generate() {

    }

    @Override
    public String getName() {
        return null;
    }
}
