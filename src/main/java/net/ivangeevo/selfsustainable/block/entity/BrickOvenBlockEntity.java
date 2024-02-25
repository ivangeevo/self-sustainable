/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.selfsustainable.block.entity;

import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.item.FuelTicksManager;
import net.ivangeevo.selfsustainable.recipe.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.screen.PropertyDelegate;
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

import java.util.*;

import static net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock.LIT;

public class BrickOvenBlockEntity extends BlockEntity implements Clearable {
    private final DefaultedList<ItemStack> itemBeingCooked = DefaultedList.ofSize(1, ItemStack.EMPTY);;

    private final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter =  RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);

    // Added variables from BTW

    private boolean lightOnNextUpdate = false;

    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace

    private final int cookTimeMultiplier = 4;
    private int visualFuelLevel = 0;
    private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4 );


    // the following is not the actual maximum time, but rather the point above which additional fuel can no longer be added
    //private final int m_iMaxFuelBurnTime = ( 1600 * 2 * 2 ); // 1600 oak log burn time, 2x base furnace multiplier, 2x brick furnace multiplier
    // the following is an actual max
    private final int maxFuelBurnTime = ((64 + 7 ) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier

    public static final int BASE_BURN_TIME_MULTIPLIER = 2;

    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;

    public static final int DEFAULT_COOK_TIME = 400;

    /** DEFINITIONS OF WHAT EACH PROPERTY MEANS **/

    // the amount of ticks that the oven has in its buffer.
    int unlitFuelBurnTime;

    // remaining time (in ticks) for which the current fuel item will continue to burn.
    int fuelBurnTime;

    // the progress (in ticks) of the cooking process for the item in the input slot of the furnace.
    private final int[] cookingTimes;

    // the total time needed for the cooked item to complete.
    private final int[] cookingTotalTimes;


    // Update fuel level
    public void updateFuelLevel(int newFuelLevel) {
        this.visualFuelLevel = MathHelper.clamp(newFuelLevel, 0, 9); // Ensure the fuel level is within valid range
        markDirty(); // Mark the block entity as dirty to save changes
    }




    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
        this.cookingTimes = new int[4];
        this.cookingTotalTimes = new int[4];
    }



    public static void litServerTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        boolean bl = false;

        for (int i = 0; i < oven.itemBeingCooked.size(); ++i) {
            ItemStack itemStack = oven.itemBeingCooked.get(i);
            if (!itemStack.isEmpty()) {
                bl = true;
                int var10002 = oven.cookingTimes[i]++;
                if (oven.cookingTimes[i] >= oven.cookingTotalTimes[i]) {
                    Inventory inventory = new SimpleInventory(itemStack);
                    ItemStack itemStack2 = oven.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                            recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);

                    if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                        oven.itemBeingCooked.set(i, itemStack2);
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

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }



    public static void unlitServerTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        boolean bl = false;

        for (int i = 0; i < oven.itemBeingCooked.size(); ++i) {
            if (oven.cookingTimes[i] > 0) {
                bl = true;
                oven.cookingTimes[i] = MathHelper.clamp(oven.cookingTimes[i] - 2, 0, oven.cookingTotalTimes[i]);
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }

    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        Random random = world.random;

        Optional<OvenCookingRecipe> optional = oven.getRecipeFor(oven.itemBeingCooked.get(0));

        // Check if the furnace was burning in the previous tick
        boolean bWasBurning = oven.fuelBurnTime > 0;
        boolean inventoryChanged = false;

        // Decrease furnace burn time if it's still burning
        if (oven.fuelBurnTime > 0) {
            --oven.fuelBurnTime;
        }

        // Check if the furnace is burning and update burn time
        if (!world.isClient) {

            if (bWasBurning || oven.lightOnNextUpdate) {
                oven.fuelBurnTime += oven.unlitFuelBurnTime;
                oven.unlitFuelBurnTime = 0;
                oven.lightOnNextUpdate = false;
            }

            // Check if the furnace is burning and can smelt
            if (bWasBurning && oven.canSmelt() && optional.isPresent()) {
                int cookTime = optional.map(OvenCookingRecipe::getCookTime).orElse(0);
                ++cookTime;

                // Check if the ovenBurnTime is greater than or equal to the cookTime
                if (optional.get().getCookTime() >= cookTime) {
                    // Consume fuel
                    oven.fuelBurnTime -= cookTime;

                    // Get the smelting result from the recipe
                    ItemStack resultStack = optional.map(recipe -> recipe.getOutput(null)).orElse(ItemStack.EMPTY);

                    // Check if the output can be inserted into the output slot
                    if (oven.itemBeingCooked.get(1).isEmpty() || ItemStack.canCombine(oven.itemBeingCooked.get(1), resultStack)) {
                        // Update the output slot
                        oven.itemBeingCooked.set(1, resultStack.copy());
                        inventoryChanged = true;
                    }
                }
            } else {
                oven.fuelBurnTime = 0;
            }

            // Check for fire spread
            if (state.get(LIT) && random.nextFloat() <= CHANCE_OF_FIRE_SPREAD) {
                BlockState frontState = world.getBlockState(pos);
                Direction facing = frontState.get(BrickOvenBlock.FACING); // Assuming FACING is the property for facing direction

                pos.offset(facing);
                //FireBlock.checkForFireSpreadAndDestructionToOneBlockLocation(world, frontPos.getX(), frontPos.getY(), frontPos.getZ());
            }

            // Update furnace block state if burning status changed
            if (bWasBurning != state.get(LIT)) {
                inventoryChanged = true;
                world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
            }

            // Update inventory and visual fuel level
            oven.updateVisualFuelLevel();
        }

        // Notify inventory change if necessary
        if (inventoryChanged) {
            oven.markDirty();
        }


        boolean hasItemToCook = false;

        for (Direction direction : Direction.Type.HORIZONTAL) {
            int j = direction.getHorizontal();

            if (!hasItemToCook && j < oven.itemBeingCooked.size() && !oven.itemBeingCooked.get(j).isEmpty() && random.nextFloat() < 0.2f) {
                float f = 0.3125f;
                double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125f);
                double e = Math.max(pos.getY() + 0.5, Math.min(pos.getY() + 1.0, (double) pos.getY() + 0.7)); // Clamped Y coordinate
                double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.25f) + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125f);

                for (int k = 0; k < 4; ++k) {
                    world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                }

                hasItemToCook = true; // Set the boolean to true to avoid spawning more than one set of particles
            }
        }




    }
    



    protected boolean canSmelt()
    {
        if (this.itemBeingCooked.get(0).getItem() == null)
        {
            return false;
        }
        else
        {
            ItemStack var1 = itemBeingCooked.get(0).copy();

            if ( var1 == null )
            {
                return false;
            }
            else if ( this.itemBeingCooked.get(2).getItem() == null )
            {
                return true;
            }
            else if ( !this.itemBeingCooked.get(2).equals(var1) )
            {
                return false;
            }
            else
            {
                int iOutputStackSizeIfCooked = itemBeingCooked.get(2).getCount() + var1.getCount();

                if ( iOutputStackSizeIfCooked <= inventory.size() && iOutputStackSizeIfCooked <= itemBeingCooked.get(2).getMaxCount()  )
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



    public DefaultedList<ItemStack> getItemBeingCooked() {
        return this.itemBeingCooked;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.itemBeingCooked.clear();
        Inventories.readNbt(nbt, this.itemBeingCooked);
        int[] is;
        if (nbt.contains("CookingTimes", 11)) {
            is = nbt.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

        if (nbt.contains("CookingTotalTimes", 11)) {
            is = nbt.getIntArray("CookingTotalTimes");
            System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

        this.fuelBurnTime = nbt.getShort("BurnTime");
        this.visualFuelLevel = nbt.getShort("VisualFuelLevel");

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.itemBeingCooked, true);
        nbt.putIntArray("CookingTimes", this.cookingTimes);
        nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);

        nbt.putShort("BurnTime", (short)this.fuelBurnTime);
        nbt.putShort("VisualFuelLevel", (short)this.visualFuelLevel);

    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.itemBeingCooked, true);
        return nbtCompound;
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack) {
        return this.itemBeingCooked
                .stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.matchGetter.getFirstMatch(new SimpleInventory(new ItemStack[]{stack}), this.world);
    }

    

    public boolean addItem(@Nullable Entity user, ItemStack stack, int cookTime) {
        for (int i = 0; i < this.itemBeingCooked.size(); ++i) {
            ItemStack itemStack = this.itemBeingCooked.get(i);
            if (itemStack.isEmpty()) {
                this.cookingTotalTimes[i] = cookTime;
                this.cookingTimes[i] = 0;
                this.itemBeingCooked.set(i, stack.split(1));
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();
                return true;
            }
        }

        return false;
    }

    @Nullable
    public ItemStack retrieveItem(@Nullable Entity user, ItemStack stack) {
        for (int i = 0; i < this.itemBeingCooked.size(); ++i) {
            ItemStack cookingStack = this.itemBeingCooked.get(i);
            if (!cookingStack.isEmpty()) {
                // Optionally handle different retrieval logic here
                this.cookingTimes[i] = 0;
                this.itemBeingCooked.set(i, ItemStack.EMPTY); // Clear the slot
                assert this.world != null;
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();

                // Return a copy of the retrieved item
                return cookingStack.copy();
            }
        }

        return ItemStack.EMPTY; // Return an empty stack if no item was retrieved
    }


    public int attemptToAddFuel(ItemStack stack) {
        int iTotalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int iDeltaBurnTime = maxFuelBurnTime - iTotalBurnTime;
        int iNumItemsBurned = 0;

        if (iDeltaBurnTime > 0) {
            int fuelTicksToAdd = getFuelTicksForItem(stack);

            // Calculate the maximum number of items that can be burned based on fuel ticks
            iNumItemsBurned = iDeltaBurnTime / fuelTicksToAdd;

            if (iNumItemsBurned == 0 && getVisualFuelLevel() <= 2) {
                // once the fuel level hits the bottom visual stage, you can jam anything in
                iNumItemsBurned = 1;
            }

            if (iNumItemsBurned > 0) {
                if (iNumItemsBurned > stack.getCount()) {
                    iNumItemsBurned = stack.getCount();
                }

                // Add the item to the furnace
                unlitFuelBurnTime += fuelTicksToAdd * iNumItemsBurned;

                markDirty();
            }
        }

        return iNumItemsBurned;
    }


    public int getFuelTicksForItem(ItemStack stack) { return getFuelItemBase(stack) * brickBurnTimeMultiplier; }

    public int getFuelItemBase(ItemStack stack)
    {

        if ( stack != null )
        {
            Identifier itemId = Registries.ITEM.getId(stack.getItem());
            return FuelTicksManager.getFuelTicks(itemId) * BASE_BURN_TIME_MULTIPLIER;
        }

        return 0;
    }




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



    public int getVisualFuelLevel() {
        return visualFuelLevel;
    }

    public void setVisualFuelLevel(int iLevel) {
        if (world != null && visualFuelLevel != iLevel ) {
            visualFuelLevel = iLevel;

            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }





    private void updateListeners() {
        this.markDirty();

        if (this.getWorld() != null)
        {
            this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public void clear() {
        this.itemBeingCooked.clear();
    }

}
