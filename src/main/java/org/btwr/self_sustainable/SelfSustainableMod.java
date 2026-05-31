package org.btwr.self_sustainable;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import org.btwr.self_sustainable.event.ModEvents;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.component.FoodComponentModifier;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.network.SyncWickerBasketS2C;
import org.btwr.self_sustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import org.btwr.self_sustainable.registry.FuelRegistryManager;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.btwr.self_sustainable.util.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Self Sustainable.");

        // Modifying foods to gave less hunger in general and 0 saturation.
        // Also, some additional negative effects to certain ones.
        FoodComponentModifier.register();

        ModBlocks.register();
        ModItems.register();
        ModSoundEvents.register();
        ModBlockEntities.register();
        ModComponentsTypes.register();
        ModItemGroup.register();
        ModEvents.register();

        ModRecipes.register();

        WorldUtils.init();

        // Modifying fuel items.
        FuelRegistryManager.initEntries();

        // register wicker basket S2C payload
        PayloadTypeRegistry.playS2C().register(SyncWickerBasketS2C.ID, SyncWickerBasketS2C.CODEC);
    }

}