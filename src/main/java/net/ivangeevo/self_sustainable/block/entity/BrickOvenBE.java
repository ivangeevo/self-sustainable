package net.ivangeevo.self_sustainable.block.entity;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.entity.util.SingleStackInventory;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BrickOvenBE extends AbstractOvenBE
{

    public BrickOvenBE(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, @NotNull BrickOvenBE ovenBE)
    {
        ItemStack cookStack = ovenBE.getStack();

        boolean bWasBurning = ovenBE.fuelBurnTime > 0;
        boolean bInvChanged = false;

        SimpleInventory inventory = new SimpleInventory(cookStack);
        ItemStack cookedStack = ovenBE.matchGetter.getFirstMatch(inventory, world)
                .map(recipe -> recipe.craft(inventory, world.getRegistryManager()))
                .orElse(cookStack);

        // Decrease furnace burn time if it's still burning
        if (ovenBE.fuelBurnTime > 0)
        {
            --ovenBE.fuelBurnTime;
        }

        if ( (state.get(LIT) && ovenBE.unlitFuelBurnTime > 0) || ovenBE.lightOnNextUpdate )
        {

            ovenBE.fuelBurnTime += ovenBE.unlitFuelBurnTime;
            ovenBE.unlitFuelBurnTime = 0;

            ovenBE.lightOnNextUpdate = false;

        }

        if ( ovenBE.isBurning() )
        {
            // Increment the cooking time
            ++ovenBE.cookTime;

            if (ovenBE.cookTime >= ovenBE.cookTimeTotal && cookedStack.isItemEnabled(world.getEnabledFeatures()))
            {
                ovenBE.setStack(cookedStack);
                bInvChanged = true;
                ovenBE.cookTime = 0;
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));

            }
        }
        else
        {
            world.setBlockState(pos, state.with(LIT, false));
            ovenBE.cookTime = 0;

        }

        ovenBE.updateVisualFuelLevel();


        if (bInvChanged)
        {
            markDirty(world, pos, state);
        }

    }


    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBE ovenBE)
    {
        setFlameParticles(world, pos, state);
    }


}
