package net.ivangeevo.self_sustainable;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.FuelTicksManager;
import net.ivangeevo.self_sustainable.networking.NetworkMessagesRegistry;
import net.ivangeevo.self_sustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger("self_sustainable");

    @Override
    public void onInitialize() {
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
    }
}
