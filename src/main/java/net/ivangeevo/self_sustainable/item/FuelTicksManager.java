package net.ivangeevo.self_sustainable.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FuelTicksManager
{
    private static final Map<Identifier, Integer> fuelTicksMap = new HashMap<>();

    // Method to register the fuel values for items set in the Json file.
    public static void loadFuelTicks()
    {
        InputStream stream = FuelTicksManager.class.getResourceAsStream("/data/self_sustainable/items/oven_fuel_items.json");

        if (stream != null)
        {
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8))
            {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject items = json.getAsJsonObject("items");

                for (Map.Entry<String, JsonElement> entry : items.entrySet())
                {
                    Identifier itemId = new Identifier(entry.getKey());
                    int fuelTicks = entry.getValue().getAsInt();
                    fuelTicksMap.put(itemId, fuelTicks);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Helper method to get the fuel amount for a specific item in the Json map file.
    public static int getFuelTicks(Item item) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }
}
