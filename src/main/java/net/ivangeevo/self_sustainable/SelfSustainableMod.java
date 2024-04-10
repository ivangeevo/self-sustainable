package net.ivangeevo.self_sustainable;

import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.blocks.TorchBlock;
import net.ivangeevo.self_sustainable.block.entity.CrudeTorchBlockEntity;
import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.FuelTicksManager;
import net.ivangeevo.self_sustainable.networking.NetworkMessagesRegistry;
import net.ivangeevo.self_sustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer
{

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger("self_sustainable");

    @Override
    public void onInitialize()
    {
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
}
