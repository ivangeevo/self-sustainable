package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;
import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FUEL_STATE;

public abstract class CampfireBEManager
{

    private static final int BASE_BURN_TIME_MULTIPLIER = 2;
    private static final int CAMPFIRE_BURN_TIME_MULTIPLIER = 8;
    private static final int DEFAULT_COOK_TIME = 400;
    private static final int TIME_TO_COOK = (DEFAULT_COOK_TIME * CAMPFIRE_BURN_TIME_MULTIPLIER *
            3 / 2 ); // this line represents efficiency relative to furnace cooking
    private static final int MAX_BURN_TIME = (5 * MiscUtils.TICKS_PER_MINUTE);
    private static final int INITIAL_BURN_TIME = (50 * 4 * CAMPFIRE_BURN_TIME_MULTIPLIER *
            BASE_BURN_TIME_MULTIPLIER); // 50 is the furnace burn time of a shaft
    private static final int WARMUP_TIME = (10 * MiscUtils.TICKS_PER_SECOND);
    private static final int REVERT_TO_SMALL_TIME = (20 * MiscUtils.TICKS_PER_SECOND);
    private static final int BLAZE_TIME = (INITIAL_BURN_TIME * 3 / 2 );
    private static final int SMOULDER_TIME = (5 * MiscUtils.TICKS_PER_MINUTE); // used to be 2 minutes
    private static final int TIME_TO_BURN_FOOD = (TIME_TO_COOK / 2 );
    private static final float CHANCE_OF_FIRE_SPREAD = 0.05F;
    private static final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.01F;

    private static int burnTimeCountdown = 0;
    private static int burnTimeSinceLit = 0;
    private static int cookCounter = 0;
    private static int smoulderCounter = 0;
    private static int cookBurningCounter = 0;


    public static void onLitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBE)
    {
        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded entity;
        entity = campfireBE;


        int iCurrentFireLevel = getCurrentFireLevel(state);

        if ( iCurrentFireLevel > 0 )
        {

            //TODO : Fire spread for campfire
            /**
            if ( iCurrentFireLevel > 1 && world.random.nextFloat() <= CHANCE_OF_FIRE_SPREAD)
            {
                FireBlock.checkForFireSpreadFromLocation(worldObj, xCoord, yCoord, zCoord, worldObj.rand, 0);
            }
             **/

            burnTimeSinceLit++;

            if (burnTimeCountdown > 0 )
            {
                burnTimeCountdown--;

                if ( iCurrentFireLevel == 3 )
                {
                    // blaze burns extra fast

                    burnTimeCountdown--;
                }
            }

            iCurrentFireLevel = validateFireLevel(world, state, pos);

            if ( iCurrentFireLevel > 0 )
            {
                //updateCookState(world, pos ,state, campfireBE);

                if (world.random.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnCampfire(world, pos) )
                {
                    extinguishFire(world, state, pos, false);
                }
            }
        }
        else if (smoulderCounter > 0 )
        {
            smoulderCounter--;

            if (smoulderCounter == 0 || world.random.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnCampfire(world, pos) )
            {
                stopSmouldering(world, pos);
            }
        }




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

    private static void updateCookState(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfireBE)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack cookStack = campfireBE.getItemsBeingCooked().get(0);
        PlayerEntity player = (PlayerEntity) cookStack.getHolder();
        assert player != null;
        Hand hand = player.getActiveHand();

        if (blockEntity instanceof CampfireBlockEntity && ((CampfireBlockEntity)blockEntity).getRecipeFor(player.getStackInHand(hand)).isPresent())
        {

            int iFireLevel = getCurrentFireLevel(state);

            if ( iFireLevel >= 2 )
            {

                cookCounter++;

                if (cookCounter >= TIME_TO_COOK)
                {
                    setCookStack(world, pos, cookStack);

                    cookCounter = 0;

                    // don't reset burn counter here, as the food can still burn after cooking
                }

                //TODO: Add burned meat logic
                /**
                if ( iFireLevel >= 3 && cookStack.itemID != BTWItems.burnedMeat.itemID )
                {
                    cookBurningCounter++;

                    if (cookBurningCounter >= TIME_TO_BURN_FOOD)
                    {
                        setCookStack(new ItemStack(BTWItems.burnedMeat));

                        cookCounter = 0;
                        cookBurningCounter = 0;
                    }
                    **/
            }
        }
    }

    public static void setCookStack(World world, BlockPos pos, ItemStack stack)
    {
        ItemStack cookStack;

        if ( stack != null )
        {

            cookStack = stack.copy();

            cookStack.setCount(1);
        }
        else
        {
            cookBurningCounter = 0;
        }

        cookCounter = 0;

        world.markDirty( pos );
    }


    public static int validateFireLevel(World world, BlockState state, BlockPos pos)
    {
        int iCurrentFireLevel = getCurrentFireLevel(state);

        if ( iCurrentFireLevel > 0 )
        {
            //int iFuelState = FCBetterThanWolves.fcBlockCampfireUnlit.GetFuelState( worldObj, xCoord, yCoord, zCoord );

            if (burnTimeCountdown <= 0 )
            {
                extinguishFire(world,state, pos,true);

                return 0;
            }
            else
            {
                int iDesiredFireLevel = 2;

                if (burnTimeSinceLit < WARMUP_TIME || burnTimeCountdown < REVERT_TO_SMALL_TIME)
                {
                    iDesiredFireLevel = 1;
                }
                else if (burnTimeCountdown > BLAZE_TIME)
                {
                    iDesiredFireLevel = 3;
                }

                if ( iDesiredFireLevel != iCurrentFireLevel )
                {
                    changeFireLevel(world, pos, state, iDesiredFireLevel);

                    if ( iDesiredFireLevel == 1 && iCurrentFireLevel == 2 )
                    {
                        Ignitable.playExtinguishSound(world, pos, false);
                    }

                    return iDesiredFireLevel;
                }
            }

        }
        else // iCurrenFireLevel == 0
        {
            if (burnTimeCountdown > 0 && state.get(FUEL_STATE) == CampfireState.SMOULDERING)
            {
                relightSmouldering(state);
                return 1;
            }
        }

        return iCurrentFireLevel;
    }

    public static void changeFireLevel(World world, BlockPos pos, BlockState state, int iFireLevel)
    {
        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).changeFireLevel(world, pos, state, iFireLevel);
    }

    private static void relightSmouldering(BlockState state)
    {
        burnTimeSinceLit = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).relightFire(state);
    }

    private static void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            smoulderCounter = SMOULDER_TIME;
        }
        else
        {
            smoulderCounter = 0;
        }

        cookCounter = 0; // reset cook counter in case fire is relit later
        cookBurningCounter = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).extinguishFire(world, state, pos, bSmoulder);
    }

    private static void stopSmouldering(World world, BlockPos pos)
    {
        smoulderCounter = 0;

        CampfireBlock block = (CampfireBlock) world.getBlockState(pos).getBlock();

        ((CampfireBlockAdded)block).stopSmouldering(world, pos);
    }

    public static boolean isRainingOnCampfire(World world, BlockPos pos)
    {
        return world.isRaining() && world.hasRain(pos);
    }

    private static int getCurrentFireLevel(BlockState state)
    {
        return state.get(FIRE_LEVEL);
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }


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
