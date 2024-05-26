/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block.entity;

import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.entity.util.OvenExtinguisher;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.FuelTicksManager;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
import org.spongepowered.asm.mixin.Unique;

import java.util.Objects;
import java.util.Optional;

import static net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock.FUEL_LEVEL;
import static net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock.LIT;

public class BrickOvenBlockEntity extends BlockEntity implements Clearable {

    /** the progress (in ticks) of the item that's currently cooking. **/
    private final int[] cookingTimes;

    /** the total ticks needed for the item that's cooking to complete. **/
    private final int[] cookingTotalTimes;
    private final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);


    // Added variables from BTW
    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    private boolean lightOnNextUpdate = false;

    private final DefaultedList<ItemStack> cookStack = DefaultedList.ofSize(1, ItemStack.EMPTY);;

    private int unlitFuelBurnTime = 0;
    private int visualFuelLevel = this.getCachedState().get(FUEL_LEVEL);

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

    /** remaining amount of ticks for which the oven will continue to burn **/
   public int ovenBurnTime = 0;

    private static final int FUEL_LEVEL_STATES = 9;

    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);


    // TEMPORARY STATS FOR A SEMI WORKING LOGIC //

    @Unique
    private int litTime = 0;

    public int getLitTime() {
        return litTime;
    }

    public void setLitTime(int value) {
        this.litTime = value;
    }

    // END OF TEMP LOGIC //

    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
        this.cookingTimes = new int[4];
        this.cookingTotalTimes = new int[4];

        // Update FUEL_LEVEL property when the block entity is initialized
        this.updateFuelLevelProperty();

    }


    // Method to update the FUEL_LEVEL property based on visualFuelLevel
    private void updateFuelLevelProperty()
    {

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

        int currentFuelLevel = this.getCachedState().get(BrickOvenBlock.FUEL_LEVEL);
        int newFuelLevel = mapVisualFuelLevelToState(this.visualFuelLevel);

        if (currentFuelLevel != newFuelLevel && world != null)
        {
            world.setBlockState(pos, this.getCachedState().with(BrickOvenBlock.FUEL_LEVEL, newFuelLevel), Block.NOTIFY_ALL);
        }

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
        BrickOvenBlockEntity oven = getOven();
        if (oven == null) {
            return DEFAULT_COOK_TIME * cookTimeMultiplier;
        }

        ItemStack itemStack = oven.cookStack.get(0);
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

    private BrickOvenBlockEntity getOven()
    {

        if (world == null || pos == null)
        {
            return null;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BrickOvenBlockEntity))
        {
            return null;
        }

        return (BrickOvenBlockEntity) blockEntity;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven)
    {

        OvenExtinguisher.onLitServerTick(world, pos, state, oven);

        Random random = world.random;
        Optional<OvenCookingRecipe> optional = oven.getRecipeFor(oven.cookStack.get(0));

        // Check if the furnace was burning in the previous tick
        boolean bWasBurning = oven.ovenBurnTime > 0;
        boolean inventoryChanged = false;

        // Decrease furnace burn time if it's still burning
        if (oven.ovenBurnTime > 0)
        {
            --oven.ovenBurnTime;
        }

        // Check if the furnace is burning and update burn time
        if (!world.isClient)
        {
            if (bWasBurning || oven.lightOnNextUpdate)
            {
                oven.ovenBurnTime += oven.unlitFuelBurnTime;
                oven.unlitFuelBurnTime = 0;

                oven.lightOnNextUpdate = false;
            }

            // Check if the furnace is burning and can smelt
            if (bWasBurning && oven.canSmelt() && optional.isPresent())
            {
                int cookTime = optional.map(OvenCookingRecipe::getCookTime).orElse(0);

                ++cookTime;

                // Check if the ovenBurnTime is greater than or equal to the cookTime
                if (oven.cookingTimes.length >= oven.getCookTimeForCurrentItem())
                {

                    // Consume fuel
                    oven.ovenBurnTime -= cookTime;

                    // Get the smelting result from the recipe
                    ItemStack resultStack = optional.map(recipe -> recipe.getOutput(null)).orElse(ItemStack.EMPTY);

                    // Check if the output can be inserted into the output slot
                    if (oven.cookStack.get(1).isEmpty() || ItemStack.canCombine(oven.cookStack.get(1), resultStack)) {
                        // Update the output slot
                        oven.cookStack.set(1, resultStack.copy());
                        inventoryChanged = true;
                    }
                }
            }
            else
            {
                oven.ovenBurnTime = 0;
            }

            // Check for fire spread
            if (state.get(LIT) && random.nextFloat() <= CHANCE_OF_FIRE_SPREAD) {
                BlockState frontState = world.getBlockState(pos);
                Direction facing = frontState.get(BrickOvenBlock.FACING); // Assuming FACING is the property for facing direction

                pos.offset(facing);
                //FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(world, frontPos.getX(), frontPos.getY(), frontPos.getZ());
            }

            // Update furnace block state if burning status changed
            if (bWasBurning != oven.isBurning())
            {
                inventoryChanged = true;
                world.setBlockState(pos, state.with(LIT, true), Block.NOTIFY_LISTENERS);
            }

            // Update inventory and visual fuel level
            oven.updateVisualFuelLevel();

        }

        // Notify inventory change if necessary
        if (inventoryChanged)
        {
            oven.markDirty();
        }



        if (!state.get(LIT)) {
            boolean bl = false;

            for (int i = 0; i < oven.cookStack.size(); ++i) {
                if (oven.cookingTimes[i] > 0) {
                    bl = true;
                    oven.cookingTimes[i] = MathHelper.clamp(oven.cookingTimes[i] - 2, 0, oven.cookingTotalTimes[i]);
                }
            }

            if (bl) {
                markDirty(world, pos, state);
            }
        } else {

            boolean bl = false;

            for (int i = 0; i < oven.cookStack.size(); ++i) {
                ItemStack itemStack = oven.cookStack.get(i);
                if (!itemStack.isEmpty()) {
                    bl = true;
                    int var10002 = oven.cookingTimes[i]++;
                    if (oven.cookingTimes[i] >= oven.cookingTotalTimes[i]) {
                        Inventory inventory = new SimpleInventory(itemStack);
                        ItemStack itemStack2 = oven.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                                recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);

                        if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                            oven.cookStack.set(i, itemStack2);
                            world.updateListeners(pos, state, state, 3);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                        }

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

     public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven)
     {
         setLargeSmokeParticles(world, pos, state, oven);
         setFlameParticles(world, pos, state);
     }

     private static void setLargeSmokeParticles(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven)
     {
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

     //Particles that display around the fuel/flame area when the block is lit.
     private static void setFlameParticles(World world, BlockPos pos, BlockState state)
     {
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
        if (this.cookStack.get(0).getItem() == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = cookStack.get(0).copy();

            if ( var1 == null )
            {
                return false;
            }
            else if ( this.cookStack.get(2).getItem() == null )
            {
                return true;
            }
            else if ( !this.cookStack.get(2).equals(var1) )
            {
                return false;
            }
            else
            {
                int iOutputStackSizeIfCooked = cookStack.get(2).getCount() + var1.getCount();

                if ( iOutputStackSizeIfCooked <= inventory.size() && iOutputStackSizeIfCooked <= cookStack.get(2).getMaxCount()  )
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


    public static DefaultedList<ItemStack> getCookStackList(BrickOvenBlockEntity ovenBE)
    {
        return ovenBE.cookStack;
    }
    public static ItemStack getCookStack0(BrickOvenBlockEntity ovenBE)
    {
        return ovenBE.cookStack.get(0);
    }
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cookStack.clear();
        Inventories.readNbt(nbt, this.cookStack);
        int[] is;
        if (nbt.contains("CookingTimes", 11)) {
            is = nbt.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

        if (nbt.contains("CookingTotalTimes", 11)) {
            is = nbt.getIntArray("CookingTotalTimes");
            System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

        if (nbt.contains("UnlitFuel")) {
            unlitFuelBurnTime = nbt.getInt("UnlitFuel");
        }



        this.visualFuelLevel = nbt.getShort("VisualFuelLevel");

        this.ovenBurnTime = nbt.getShort("BurnTime");


        if (nbt.contains("LitTime"))
        {
            litTime = nbt.getInt("LitTime");
        }

    }

    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.cookStack, true);
        nbt.putIntArray("CookingTimes", this.cookingTimes);
        nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);

        nbt.putShort("BurnTime", (short)this.ovenBurnTime);
        nbt.putInt("UnlitFuel", this.unlitFuelBurnTime);

        nbt.putShort("VisualFuelLevel", (short)this.visualFuelLevel);

        nbt.putInt("LitTime", litTime);


    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt()
    {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.cookStack, true);
        return nbtCompound;
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack)
    {
        return this.cookStack.stream().noneMatch(ItemStack::isEmpty) ?
                Optional.empty() : this.matchGetter
                .getFirstMatch(new SimpleInventory(stack), this.world);
    }



    public boolean addItem(@Nullable Entity user, ItemStack stack, int cookTime)
    {
        for (int i = 0; i < this.cookStack.size(); ++i) {
            ItemStack itemStack = this.cookStack.get(i);
            if (itemStack.isEmpty()) {
                this.cookingTotalTimes[i] = cookTime;
                this.cookingTimes[i] = 0;
                this.cookStack.set(i, stack.split(1));
                assert this.world != null;
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();
                return true;
            }
        }

        return false;
    }

    public static void retrieveItem(BrickOvenBlockEntity ovenBE, PlayerEntity player) {
        DefaultedList<ItemStack> itemsBeingCooked = ovenBE.cookStack;
        if (!itemsBeingCooked.isEmpty()) {
            ItemStack itemStack = itemsBeingCooked.get(0);
            if (!itemStack.isEmpty())
            {
                player.giveItemStack(itemStack);
                itemsBeingCooked.set(0, ItemStack.EMPTY);
                ovenBE.markDirty();
                Objects.requireNonNull(ovenBE.getWorld()).updateListeners(ovenBE.getPos(), ovenBE.getCachedState(), ovenBE.getCachedState(), Block.NOTIFY_ALL);
            }
        }
    }



    public int attemptToAddFuel(ItemStack stack)
    {
        int iTotalBurnTime = unlitFuelBurnTime + ovenBurnTime;
        int iDeltaBurnTime = maxFuelBurnTime - iTotalBurnTime;
        int iNumItemsBurned = 0;

        if (iDeltaBurnTime > 0)
        {

            // Calculate the maximum number of items that can be burned based on fuel ticks
            iNumItemsBurned = iDeltaBurnTime / getItemBurnTime(stack);

            if (iNumItemsBurned == 0 && this.getVisualFuelLevel() <= 2)
            {
                // once the fuel level hits the bottom visual stage, you can jam anything in
                iNumItemsBurned = 1;
            }

            if (iNumItemsBurned > 0) {
                if (iNumItemsBurned > stack.getCount())
                {
                    iNumItemsBurned = stack.getCount();
                }

                // Add the item to the furnace
                unlitFuelBurnTime += getItemBurnTime(stack) * iNumItemsBurned;

                markDirty();
            }
        }

        return iNumItemsBurned;
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

    public boolean isBurning()
    {
        return this.ovenBurnTime > 0;
    }


    public void updateVisualFuelLevel()
    {
        int iTotalBurnTime = unlitFuelBurnTime + ovenBurnTime;

        int iNewFuelLevel = 0;

        if (iTotalBurnTime > 0)
        {
            if (iTotalBurnTime < visualSputterFuelLevel)
            {
                iNewFuelLevel = 1;
            }
            else
            {
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
        this.cookStack.clear();
    }

}
