package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Objects;

public abstract class CampfireBEManager
{
    public static void onLitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBE)
    {
        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded entity;
        entity = campfireBE;

        boolean bInvChanged = false;

        for (int i = 0; i < campfireBE.getItemsBeingCooked().size(); ++i)
        {
            SimpleInventory inventory;
            ItemStack itemStack2;
            ItemStack itemStack = campfireBE.getItemsBeingCooked().get(i);
            if (itemStack.isEmpty()) continue;
            bInvChanged = true;
            int n = i;
            campfireBE.cookingTimes[n] = campfireBE.cookingTimes[n] + 1;
            if (campfireBE.cookingTimes[i] < campfireBE.cookingTotalTimes[i] || !(itemStack2 = campfireBE.matchGetter.getFirstMatch(inventory = new SimpleInventory(itemStack), world).map(recipe -> recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack)).isItemEnabled(world.getEnabledFeatures())) continue;
            campfireBE.getItemsBeingCooked().set(i, itemStack2);
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
        }

        if (bInvChanged)
        {
            markDirty(world, pos, state);
        }
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }
    /**
    public static void onLitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBE)
    {
        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded entity;
        entity = campfireBE;

        boolean bInvChanged = false;

        for (int i = 0; i < campfireBE.getItemsBeingCooked().size(); ++i)
        {
            ItemStack cookStack = campfireBE.getItemsBeingCooked().get(i);
            if (!cookStack.isEmpty())
            {
                bInvChanged = true;
                if (campfireBE.cookingTimes[i] >= campfireBE.cookingTotalTimes[i])
                {
                    Inventory inventory = new SimpleInventory(cookStack);
                    ItemStack tempStack = campfireBE.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                            recipe.craft(inventory, world.getRegistryManager())).orElse(cookStack);

                    if (tempStack.isItemEnabled(world.getEnabledFeatures())) {
                        campfireBE.getItemsBeingCooked().set(i, tempStack);
                        world.updateListeners(pos, state, state, 3);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    }

                }
            }
        }

        if (bInvChanged)
        {
            campfireBE.markDirty();
        }

    }
     **/

    public static void onClientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBE)
    {
        Random random = world.random;

        // Random Smoke Particle Generation
        if (random.nextFloat() < 0.11f)
        {
            CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
        }

        // Smoke Particles for Cooking Items
        if (!campfireBE.getItemsBeingCooked().get(0).isEmpty() && random.nextFloat() < 0.2f)
        {
            Direction direction = state.get(CampfireBlock.FACING);
            float offset = 0.3125f;
            double d = pos.getX() + 0.5;
            double e = pos.getY() + 1.0; // Centered on Y axis at 1.0
            double g = pos.getZ() + 0.5;

            world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
        }
    }

    public static boolean addItem(Entity user, ItemStack stack, int cookTime, CampfireBlockEntity be)
    {
        be.cookingTotalTimes[0] = cookTime;
        be.cookingTimes[0] = 0;
        be.getItemsBeingCooked().set(0, stack.split(1));
        be.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, be.getPos(), GameEvent.Emitter.of(user, be.getCachedState()));
        be.getWorld().updateListeners(be.getPos(), be.getCachedState(), be.getCachedState() , Block.NOTIFY_ALL);

        return true;

    }


}
