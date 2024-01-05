package ivangeevo.selfsustainable;

import ivangeevo.selfsustainable.block.ModBlocks;
import ivangeevo.selfsustainable.entity.ModEntities;
import ivangeevo.selfsustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger("self_sustainable");

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModEntities.Blocks.registerBlockEntities();
        ModRecipes.registerRecipes();

    }
}
