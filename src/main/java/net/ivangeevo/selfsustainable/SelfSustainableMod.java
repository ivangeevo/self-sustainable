package net.ivangeevo.selfsustainable;

import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.ivangeevo.selfsustainable.block.ModBlocks;
import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.item.FuelTicksManager;
import net.ivangeevo.selfsustainable.networking.NetworkMessagesRegistry;
import net.ivangeevo.selfsustainable.recipe.ModRecipes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelfSustainableMod implements ModInitializer {

    public static final String MOD_ID = "self_sustainable";
    public static final Logger LOGGER = LoggerFactory.getLogger("self_sustainable");

    @Override
    public void onInitialize() {
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();
        ModBlockEntities.registerBlockEntities();
        ModRecipes.registerRecipes();

        NetworkMessagesRegistry.registerS2CPackets();
        NetworkMessagesRegistry.registerC2SPackets();
        // Class registering item fuel values for the Brick oven.
        FuelTicksManager.loadFuelTicks();
    }
}
