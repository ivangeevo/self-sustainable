package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {

    // Adds a check for updating the results for the Wicker Weaving recipes.

    // This is needed for compatibility to be able to craft these recipes
    // in a vanilla crafting recipe workstations
    @Inject(method = "updateResult", at = @At("TAIL"))
    private static void injectWickerWeavingSupport(
            ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci
    ) {
        if (world.isClient) return;
        if (!resultInventory.getStack(0).isEmpty()) return;

        MinecraftServer server = world.getServer();
        if (server == null) return;

        CraftingRecipeInput input = craftingInventory.createRecipeInput();
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        Optional<RecipeEntry<ShapedRecipeWithDamage>> match =
                server.getRecipeManager().getFirstMatch(ShapedRecipeWithDamage.Type.INSTANCE, input, world);

        if (match.isPresent()) {
            RecipeEntry<ShapedRecipeWithDamage> recipeEntry = match.get();
            ShapedRecipeWithDamage matchedRecipe = recipeEntry.value();

            if (resultInventory.shouldCraftRecipe(world, serverPlayer, recipeEntry)) {
                ItemStack result = matchedRecipe.craft(input, world.getRegistryManager());
                if (result.isItemEnabled(world.getEnabledFeatures())) {
                    resultInventory.setStack(0, result);
                    handler.setPreviousTrackedSlot(0, result);
                    serverPlayer.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, result));
                }
            }
        }
    }

}