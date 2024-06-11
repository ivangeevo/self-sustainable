package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

public class BrickOvenBE extends AbstractOvenBE
{

    public BrickOvenBE(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, @NotNull BrickOvenBE ovenBE)
    {
        ItemStack cookStack = ovenBE.getCookStack();

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
            world.setBlockState(pos, state.with(LIT, true));
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
        setParticles(world, pos, state);
    }


}
