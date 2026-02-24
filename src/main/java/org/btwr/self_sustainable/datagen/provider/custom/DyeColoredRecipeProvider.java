package org.btwr.self_sustainable.datagen.provider.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.SelfSustainableMod;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DyeColoredRecipeProvider implements DataProvider {

    public record Entry(String recipeBaseName, Item output, Map<DyeColor, Item> inputs, String[] pattern) {}

    private final FabricDataOutput output;
    private final List<Entry> entries;

    public DyeColoredRecipeProvider(FabricDataOutput output, List<Entry> entries) {
        this.output = output;
        this.entries = entries;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Entry entry : entries) {
            entry.inputs().forEach((dyeColor, inputItem) -> {
                Path path = output.getPath()
                        .resolve("data")
                        .resolve(SelfSustainableMod.MOD_ID)
                        .resolve("recipe")
                        .resolve(dyeColor.getName() + "_" + entry.recipeBaseName() + ".json");

                futures.add(DataProvider.writeToPath(writer, buildJson(entry, inputItem, dyeColor), path));
            });
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private JsonObject buildJson(Entry entry, Item inputItem, DyeColor dyeColor) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "minecraft:crafting_shaped");

        JsonArray pattern = new JsonArray();
        for (String row : entry.pattern()) pattern.add(row);
        json.add("pattern", pattern);

        JsonObject key = new JsonObject();
        JsonObject keyEntry = new JsonObject();
        keyEntry.addProperty("item", Registries.ITEM.getId(inputItem).toString());
        key.add("#", keyEntry);
        json.add("key", key);

        JsonObject result = new JsonObject();
        result.addProperty("id", Registries.ITEM.getId(entry.output()).toString());
        result.addProperty("count", 1);

        JsonObject components = new JsonObject();
        JsonObject dyedColor = new JsonObject();
        dyedColor.addProperty("rgb", dyeColor.getEntityColor() & 0x00FFFFFF);
        dyedColor.addProperty("show_in_tooltip", false);
        components.add("minecraft:dyed_color", dyedColor);
        result.add("components", components);
        json.add("result", result);

        return json;
    }

    @Override
    public String getName() {
        return "Dye Colored Recipes";
    }
}