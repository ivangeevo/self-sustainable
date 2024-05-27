/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.FuelTicksManager;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Clearable;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock.FUEL_LEVEL;
import static net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock.LIT;

public class BrickOvenBE extends BlockEntity implements Clearable {

    /** the progress (in ticks) of the item that's currently cooking. **/
    private int cookingTime;

    /** the total ticks needed for the item that's cooking to complete. **/
    private int cookingTotalTime;

    /** remaining amount of ticks for which the oven will continue to burn **/
    private int unlitFuelBurnTime = 0;

    /** the amount of fuel in the oven before it's lit **/

    private int fuelBurnTime = 0;


    private final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);


    // Added variables from BTW
    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    private boolean lightOnNextUpdate = false;

    private ItemStack cookStack = ItemStack.EMPTY;

    private int visualFuelLevel;

    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace
    private final int cookTimeMultiplier = 4;

    // the following is not the actual maximum time, but rather the point above which additional fuel can no longer be added
    //private final int m_iMaxFuelBurnTime = ( 1600 * 2 * 2 ); // 1600 oak log burn time, 2x base furnace multiplier, 2x brick furnace multiplier
    // the following is an actual max
    private final int maxFuelBurnTime = ((64 + 7 ) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier

    private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4 );

    public static final int BASE_BURN_TIME_MULTIPLIER = 2;

    public static final int DEFAULT_COOK_TIME = 400;



    private static final int FUEL_LEVEL_STATES = 9;

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public int getFuelBurnTime()
    {
        return fuelBurnTime;
    }

    public int setFuelBurnTime(int fuelTicks)
    {
        return fuelBurnTime += fuelTicks;
    }

    public int getCookingTime()
    {
        return cookingTime;
    }

    public int setCookingTime(int newValue)
    {
        return cookingTime = newValue;
    }

    public int getCookingTotalTime()
    {
        return cookingTotalTime;
    }

    public int setCookingTotalTime(int newValue)
    {
        return cookingTotalTime = newValue;
    }

    public boolean isBurning()
    {
       return this.getFuelBurnTime() > 0;
    }


    public BrickOvenBE(BlockPos pos, BlockState state)
    {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
    }


    // Method to update the FUEL_LEVEL property based on visualFuelLevel
    private void updateFuelLevelProperty()
    {
        if (world != null) {
            int newFuelLevel = mapVisualFuelLevelToState(this.visualFuelLevel);
            BlockState currentState = this.getCachedState();
            if (currentState.get(FUEL_LEVEL) != newFuelLevel) {
                world.setBlockState(pos, currentState.with(FUEL_LEVEL, newFuelLevel), Block.NOTIFY_ALL);
            }
        }
    }

    // Method to map visualFuelLevel to FUEL_LEVEL state
    private int mapVisualFuelLevelToState(int visualFuelLevel)
    {
        // Calculate the size of each state range
        int stateRange = maxFuelBurnTime / FUEL_LEVEL_STATES;
        // Determine the state based on the visualFuelLevel
        int newState = visualFuelLevel / stateRange;
        // Ensure the newState is within the valid range
        return Math.min(newState, FUEL_LEVEL_STATES - 1);
    }


    // Method to set the visualFuelLevel and update the FUEL_LEVEL property
    public void setVisualFuelLevel(int visualFuelLevel) {
        this.visualFuelLevel = visualFuelLevel;
        updateFuelLevelProperty();
    }

/**
    public void setVisualFuelLevel(int iLevel)
    {
        if (visualFuelLevel != iLevel)
        {
            visualFuelLevel = iLevel;
            updateListeners();
        }
    }
 **/

    protected int getCookTimeForCurrentItem()
    {
        BrickOvenBE oven = getOven();
        if (oven == null) {
            return DEFAULT_COOK_TIME * cookTimeMultiplier;
        }

        ItemStack itemStack = oven.cookStack;
        if (itemStack.isEmpty()) {
            return DEFAULT_COOK_TIME * cookTimeMultiplier;
        }

        Optional<OvenCookingRecipe> optional = oven.getRecipeFor(itemStack);
        if (optional.isEmpty()) {
            return DEFAULT_COOK_TIME * cookTimeMultiplier;
        }

        int iCookTimeShift = optional.get().getCookTime();
        return (DEFAULT_COOK_TIME << iCookTimeShift) * cookTimeMultiplier;
    }

    private BrickOvenBE getOven()
    {

        if (world == null || pos == null)
        {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BrickOvenBE))
        {
            return null;
        }

        return (BrickOvenBE) blockEntity;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BrickOvenBE ovenBE)
    {

        //OvenBlockManager.handleTempExtinguishingLogic(world, pos, state, oven);

        Random random = world.random;
        Optional<OvenCookingRecipe> optional = ovenBE.getRecipeFor(ovenBE.cookStack);

        // Check if the furnace was burning in the previous tick
        boolean bWasBurning = ovenBE.getFuelBurnTime() > 0;
        boolean inventoryChanged = false;

        // Decrease furnace burn time if it's still burning
        if (ovenBE.getFuelBurnTime() > 0)
        {
            ovenBE.setFuelBurnTime(-1);
        }

        // Check if the furnace is burning and update burn time
        if (!world.isClient())
        {
            if (bWasBurning || ovenBE.lightOnNextUpdate)
            {
                ovenBE.fuelBurnTime += ovenBE.unlitFuelBurnTime;
                ovenBE.unlitFuelBurnTime = 0;

                ovenBE.lightOnNextUpdate = false;
            }

            // Check if the furnace is burning and can smelt
            if (bWasBurning && ovenBE.canSmelt() && optional.isPresent())
            {
                int cookTime = optional.map(OvenCookingRecipe::getCookTime).orElse(0);

                ++cookTime;

                // Check if the ovenBurnTime is greater than or equal to the cookTime
                if (ovenBE.cookingTime >= ovenBE.getCookTimeForCurrentItem())
                {

                    // Consume fuel
                    ovenBE.fuelBurnTime -= cookTime;

                    // Get the smelting result from the recipe
                    ItemStack resultStack = optional.map(recipe -> recipe.getOutput(null)).orElse(ItemStack.EMPTY);

                    // Check if the output can be inserted into the output slot
                    if (ovenBE.cookStack.isEmpty() || ItemStack.canCombine(ovenBE.cookStack, resultStack)) {
                        // Update the output slot
                        ovenBE.setCookStack(resultStack.copy());
                        inventoryChanged = true;
                    }
                }
            }
            else
            {
                ovenBE.fuelBurnTime = 0;
            }

            /**
            // Check for fire spread
            if (state.get(LIT) && random.nextFloat() <= CHANCE_OF_FIRE_SPREAD) {
                BlockState frontState = world.getBlockState(pos);
                Direction facing = frontState.get(BrickOvenBlock.FACING); // Assuming FACING is the property for facing direction

                pos.offset(facing);
                //FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(world, frontPos.getX(), frontPos.getY(), frontPos.getZ());
            }
             **/

            // Update furnace block state if burning status changed
            if (bWasBurning != ovenBE.isBurning())
            {
                inventoryChanged = true;
                world.setBlockState(pos, state.with(FUEL_LEVEL, ovenBE.getFuelBurnTime()), Block.NOTIFY_LISTENERS);
            }

            // Update inventory and visual fuel level
            ovenBE.updateVisualFuelLevel();

        }

        // Notify inventory change if necessary
        if (inventoryChanged)
        {
            ovenBE.markDirty();
        }



        if (!state.get(LIT)) {
            boolean bl = false;

                if (ovenBE.cookingTime > 0) {
                    bl = true;
                    ovenBE.cookingTime = MathHelper.clamp(ovenBE.cookingTime - 2, 0, ovenBE.cookingTotalTime);
                }


            if (bl) {
                markDirty(world, pos, state);
            }
        } else {

            boolean bl = false;


                ItemStack itemStack = ovenBE.cookStack;
                if (!itemStack.isEmpty()) {
                    bl = true;
                    int var10002 = ovenBE.cookingTime++;
                    if (ovenBE.getCookingTime() >= ovenBE.getCookingTotalTime()) {
                        Inventory inventory = new SimpleInventory(itemStack);
                        ItemStack itemStack2 = ovenBE.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                                recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);

                        if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                            ovenBE.setCookStack(itemStack2);
                            world.updateListeners(pos, state, state, 3);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                        }

                    }
                }


            if (bl) {
                markDirty(world, pos, state);
            }
        }

    }



    /**
    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        Random random = world.random;

        boolean hasItemToCook = false;

        for (Direction direction : Direction.Type.HORIZONTAL)
        {

            int j = direction.getHorizontal();

            if (state.get(LIT) && !hasItemToCook && j < oven.cookStack.size() && !oven.cookStack.get(j).isEmpty() && random.nextFloat() < 0.2f)
            {
                double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125f);
                double e = Math.max(pos.getY() + 0.5, Math.min(pos.getY() + 1.0, (double) pos.getY() + 0.7)); // Clamped Y coordinate
                double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125f);

                for (int k = 0; k < 4; ++k)
                {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                }

                hasItemToCook = true; // Set the boolean to true to avoid spawning more than one set of particles
            }
        }

    }
     **/

     public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBE oven)
     {
         setLargeSmokeParticles(world, pos, state, oven);
         setFlameParticles(world, pos, state);
     }

     private static void setLargeSmokeParticles(World world, BlockPos pos, BlockState state, BrickOvenBE oven)
     {
         Random random = world.random;

         boolean hasItemToCook = false;

         for (Direction direction : Direction.Type.HORIZONTAL)
         {

             int j = direction.getHorizontal();

             if (state.get(LIT) && !hasItemToCook  && !oven.cookStack.isEmpty() && random.nextFloat() < 0.2f)
             {
                 double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125f);
                 double e = Math.max(pos.getY() + 0.5, Math.min(pos.getY() + 1.0, (double) pos.getY() + 0.7)); // Clamped Y coordinate
                 double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125f);

                 for (int k = 0; k < 4; ++k)
                 {
                     world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                 }

                 hasItemToCook = true; // Set the boolean to true to avoid spawning more than one set of particles
             }
         }
     }

     //Particles that display around the fuel/flame area when the block is lit.
     private static void setFlameParticles(World world, BlockPos pos, BlockState state) {
         if (!state.get(LIT)) {
             return;
         }

         double d = (double) pos.getX() + 0.5;
         double e = pos.getY();
         double f = (double) pos.getZ() + 0.5;

         // Adjust the frequency of particle spawning
         if (world.getRandom().nextDouble() < 0.05) { // 0.1 becomes 0.05 for half the frequency
             world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                     SoundCategory.BLOCKS, 1.0f, 1.0f, false);
             Direction direction = state.get(BrickOvenBlock.FACING);
             Direction.Axis axis = direction.getAxis();
             double g = 0.52;
             double h = world.getRandom().nextDouble() * 0.6 - 0.3;
             double i = axis == Direction.Axis.X ? (double) direction.getOffsetX() * 0.52 : h;
             double j = world.getRandom().nextDouble() * 6.0 / 16.0;
             double k = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * 0.52 : h;
             world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
             world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0);
         }
     }



    protected boolean canSmelt()
    {
        if (this.cookStack.getItem() == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = cookStack.copy();

            if ( var1 == null )
            {
                return false;
            }
            else if ( this.cookStack.getItem() == null )
            {
                return true;
            }
            else if ( !this.cookStack.equals(var1) )
            {
                return false;
            }
            else
            {
                int iOutputStackSizeIfCooked = cookStack.getCount() + var1.getCount();

                if ( iOutputStackSizeIfCooked <= inventory.size() && iOutputStackSizeIfCooked <= cookStack.getMaxCount()  )
                {
                    return true;
                }
                else
                {
                    return iOutputStackSizeIfCooked <= var1.getMaxCount();
                }
            }
        }
    }


    public ItemStack getCookStack()
    {
        return cookStack;
    }

    public ItemStack setCookStack(ItemStack newStack) {
       return this.cookStack = newStack;
    }


    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        //cookStack = ItemStack.EMPTY;
        if (nbt.contains("CookStack")) { cookStack = ItemStack.fromNbt(nbt.getCompound("CookStack")); }

        if (nbt.contains("CookingTime")) { cookingTime = nbt.getInt("CookingTime"); }
        if (nbt.contains("CookingTotalTime")) { cookingTime = nbt.getInt("CookingTotalTime"); }
        if (nbt.contains("UnlitFuel")) { unlitFuelBurnTime = nbt.getInt("UnlitFuel"); }
        if (nbt.contains("VisualFuelLevel")) { visualFuelLevel = nbt.getInt("VisualFuelLevel"); }

        this.fuelBurnTime = nbt.getShort("FuelBurnTime");


       // if (nbt.contains("LitTime")) { litTime = nbt.getInt("LitTime"); }

    }

    protected void writeNbt(NbtCompound nbt)
    {
        writeCookStack(nbt, this.cookStack, true);
        nbt.putInt("CookingTime", this.cookingTime);
        nbt.putInt("CookingTotalTime", this.cookingTotalTime);

        nbt.putShort("FuelBurnTime", (short)this.fuelBurnTime);
        nbt.putInt("UnlitFuel", this.unlitFuelBurnTime);

        nbt.putInt("VisualFuelLevel", visualFuelLevel);

        //nbt.putInt("LitTime", litTime);


    }


    public static void writeCookStack(NbtCompound nbt, ItemStack stack, boolean setIfEmpty) {
        if (!stack.isEmpty()) {
            NbtCompound stackTag = new NbtCompound();
            stack.writeNbt(stackTag);
            nbt.put("CookStack", stackTag);
        }
        else if (setIfEmpty)
        {
            nbt.put("CookStack", new NbtCompound()); // Empty compound to indicate empty ItemStack
        }
    }


    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt()
    {
        NbtCompound nbtCompound = new NbtCompound();
        writeCookStack(nbtCompound, this.cookStack, true);
        return nbtCompound;
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack)
    {
        return this.getCookStack() == ItemStack.EMPTY ?
                Optional.empty() : this.matchGetter
                .getFirstMatch(new SimpleInventory(stack), this.world);
    }



    public void addItem(@Nullable Entity user, ItemStack stack, int cookTime)
    {
            if (getCookStack().isEmpty() && this.world != null)
            {
                this.setCookingTotalTime(cookTime);
                this.setCookingTime(0);
                this.setCookStack(stack.split(1));
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();
            }


    }

    public void retrieveItem(BrickOvenBE ovenBE, PlayerEntity player) {
        ItemStack itemsBeingCooked = ovenBE.cookStack;
        if (!itemsBeingCooked.isEmpty()) {

            setCookStack(ItemStack.EMPTY);
            player.giveItemStack(itemsBeingCooked);
            ovenBE.markDirty();
            this.updateListeners();

        }
    }



    public int attemptToAddFuel(ItemStack stack) {
        int totalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int deltaBurnTime = maxFuelBurnTime - totalBurnTime;
        int numItemsBurned = 0;

        // Get the burn time for the item from the fuel map
        Integer itemBurnTime = BrickOvenBlock.FUEL_TIME_MAP.get(stack.getItem());

        if (itemBurnTime == null) {
            // Item is not a valid fuel
            return 0;
        }

        if (deltaBurnTime > 0) {
            // Calculate the maximum number of items that can be burned based on fuel ticks
            numItemsBurned = deltaBurnTime / itemBurnTime;

            if (numItemsBurned == 0 && this.getVisualFuelLevel() <= 2) {
                // Once the fuel level hits the bottom visual stage, you can jam anything in
                numItemsBurned = 1;
            }

            if (numItemsBurned > 0) {
                if (numItemsBurned > stack.getCount()) {
                    numItemsBurned = stack.getCount();
                }

                // Add the item to the furnace
                unlitFuelBurnTime += itemBurnTime * numItemsBurned;

                markDirty();
            }
        }

        return numItemsBurned;
    }


    public int getItemBurnTime(ItemStack stack)
    {
        return getItemBurnTimeBase(stack) * brickBurnTimeMultiplier;
    }

    public int getItemBurnTimeBase(ItemStack stack)
    {

        if ( stack != null )
        {
            Identifier itemId = Registries.ITEM.getId(stack.getItem());
            return FuelTicksManager.getFuelTicks(itemId) * BASE_BURN_TIME_MULTIPLIER;
        }

        return 0;
    }


    // Method to update the visual fuel level based on current burn times
    public void updateVisualFuelLevel() {
        int iTotalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int iNewFuelLevel = 0;

        if (iTotalBurnTime > 0) {
            if (iTotalBurnTime < visualSputterFuelLevel) {
                iNewFuelLevel = 1;
            } else {
                iNewFuelLevel = (iTotalBurnTime / visualFuelLevelIncrement) + 2;
            }
        }

        setVisualFuelLevel(iNewFuelLevel);
    }



    public int getVisualFuelLevel()
    {
        return visualFuelLevel;
    }







    private void updateListeners() {
        markDirty();

        if (this.getWorld() != null)
        {
            this.getWorld().updateListeners(getPos(), getCachedState(), getCachedState(), 3);
        }
    }

    public void clear() {
        this.cookStack = null;
    }

}
