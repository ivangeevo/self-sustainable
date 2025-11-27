package net.ivangeevo.self_sustainable;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.event.ModLootTableReplacement;
import net.ivangeevo.self_sustainable.item.ModItems;
import net.ivangeevo.self_sustainable.item.component.FoodComponentModifier;
import net.ivangeevo.self_sustainable.item.component.ModComponentsTypes;
import net.ivangeevo.self_sustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.ivangeevo.self_sustainable.registry.FuelRegistryManager;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Initializing Self Sustainable.");

        ModLootTableReplacement.initialize();

        // Modifying foods to gave less hunger in general and 0 saturation.
        // Also, some additional negative effects to certain ones.
        FoodComponentModifier.register();

        ModBlocks.registerBlocks();
        ModBlocks.registerItemsPlaceableAsBlocks();
        ModBlocks.registerTorchHandler();
        ModItems.register();
        ModComponentsTypes.register();
        ModItemGroup.register();
        ModBlockEntities.register();
        ModRecipes.register();

        WorldUtils.init();

        // Modifying fuel items.
        FuelRegistryManager.initEntries();

        // Predicates
        //ModelPredicateProviderRegistry.register(ModItems.TORCH, new Identifier("Lit"), (stack, world, entity, seed) -> TorchBlockEntity.isLit(stack) ? 1.0f : 0.0f);
        //ModelPredicateProviderRegistry.register(ModItems.CRUDE_TORCH, new Identifier("Lit"), (stack, world, entity, seed) -> CrudeTorchBlockEntity.isLit(stack) ? 1.0f : 0.0f);
    }

}