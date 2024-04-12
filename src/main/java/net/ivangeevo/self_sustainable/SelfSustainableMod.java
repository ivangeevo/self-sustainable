package net.ivangeevo.self_sustainable;

import com.google.gson.Gson;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.config.SSSettings;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.FuelTicksManager;
import net.ivangeevo.self_sustainable.networking.NetworkMessagesRegistry;
import net.ivangeevo.self_sustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SelfSustainableMod implements ModInitializer
{

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger("self_sustainable");

    public SSSettings settings;
    private static SelfSustainableMod instance;

    public static SelfSustainableMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize()
    {
        LOGGER.info("Initializing Self Sustainable.");
        loadSettings();
        instance = this;

        ModItems.registerModItems();
        ModItemGroup.registerItemGroups();
        ModBlocks.registerModBlocks();
        ModBlockEntities.registerBlockEntities();
        ModRecipes.registerRecipes();

        NetworkMessagesRegistry.registerS2CPackets();
        NetworkMessagesRegistry.registerC2SPackets();

        WorldUtils.init();

        // Class registering item fuel values for the Brick oven.
        FuelTicksManager.loadFuelTicks();

        //ModelPredicateProviderRegistry.register(ModItems.TORCH, new Identifier("Lit"), (stack, world, entity, seed) -> TorchBlockEntity.isLit(stack) ? 1.0f : 0.0f);
        //ModelPredicateProviderRegistry.register(ModItems.CRUDE_TORCH, new Identifier("Lit"), (stack, world, entity, seed) -> CrudeTorchBlockEntity.isLit(stack) ? 1.0f : 0.0f);

    }

    public void loadSettings() {
        File file = new File("./config/btwr/selfSustainableCommon.json");
        Gson gson = new Gson();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                settings = gson.fromJson(fileReader, SSSettings.class);
                fileReader.close();
            } catch (IOException e) {
                LOGGER.warn("Could not load Tough Environment settings: " + e.getLocalizedMessage());
            }
        } else {
            settings = new SSSettings();
        }
    }

    public void saveSettings() {
        Gson gson = new Gson();
        File file = new File("./config/btwr/selfSustainableCommon.json");
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(settings));
            fileWriter.close();
        } catch (IOException e) {
            LOGGER.warn("Could not save Tough Environment settings: " + e.getLocalizedMessage());
        }
    }
}
