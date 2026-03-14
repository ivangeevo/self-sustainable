package org.btwr.self_sustainable.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At("TAIL"))
    private static void injectSupport(
            ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory,
            CraftingResultInventory resultInventory, @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo ci
    ) {
        if (world.isClient) return;
        if (!resultInventory.getStack(0).isEmpty()) return;

        MinecraftServer server = world.getServer();
        if (server == null) return;

        CraftingRecipeInput input = craftingInventory.createRecipeInput();
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;

        // Add compatibility for these recipe types
        //addCompatFor(KnittingRecipe.Type.INSTANCE, input, world, handler, resultInventory, serverPlayer, server);
    }

    @Unique
    private static <T extends CraftingRecipe> void addCompatFor(
            RecipeType<T> type,
            CraftingRecipeInput input,
            World world,
            ScreenHandler handler,
            CraftingResultInventory resultInventory,
            ServerPlayerEntity serverPlayer,
            MinecraftServer server
    ) {
        if (!resultInventory.getStack(0).isEmpty()) return; // stop if a previous type already filled the slot

        server.getRecipeManager()
                .getFirstMatch(type, input, world)
                .ifPresent(recipeEntry -> {
                    if (resultInventory.shouldCraftRecipe(world, serverPlayer, recipeEntry)) {
                        ItemStack result = recipeEntry.value().craft(input, world.getRegistryManager());
                        if (result.isItemEnabled(world.getEnabledFeatures())) {
                            resultInventory.setStack(0, result);
                            handler.setPreviousTrackedSlot(0, result);
                            serverPlayer.networkHandler.sendPacket(
                                    new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), 0, result)
                            );
                        }
                    }
                });
    }
}