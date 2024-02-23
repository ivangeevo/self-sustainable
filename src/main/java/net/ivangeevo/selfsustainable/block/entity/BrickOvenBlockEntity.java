/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.selfsustainable.block.entity;

import com.google.common.collect.Maps;
import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.item.interfaces.ItemAdded;
import net.ivangeevo.selfsustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.selfsustainable.util.ItemUtils;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
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


    private final int[] cookingTimes;
    private final int[] cookingTotalTimes;
    private final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter =  RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);

    // Added variables from BTW

    private boolean lightOnNextUpdate = false;


    private Map<Item, Integer> fuelTimeMap;
    public ItemStack cookStack;

    protected ItemStack[] furnaceItemStacks = new ItemStack[3];

    private int unlitFuelBurnTime = 0;

    private int visualFuelLevel = 0;

    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace

    private final int cookTimeMultiplier = 4;

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
    // remaining time (in ticks) for which the current fuel item will continue to burn.
    int ovenBurnTime;

    // the total burn time (in ticks) that the current fuel item could provide when initially inserted into the furnace.
    int fuelTime;
    // the progress (in ticks) of the cooking process for the item in the input slot of the furnace.
    int cookTime;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BrickOvenBlockEntity.this.ovenBurnTime;
                case 1 -> BrickOvenBlockEntity.this.fuelTime;
                case 2 -> BrickOvenBlockEntity.this.cookTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    BrickOvenBlockEntity.this.ovenBurnTime = value;
                    break;
                }
                case 1: {
                    BrickOvenBlockEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    BrickOvenBlockEntity.this.cookTime = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };




    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
        this.cookingTimes = new int[4];
        this.cookingTotalTimes = new int[4];
        this.fuelTimeMap = createFuelTimeMap();
    }

    public void givePlayerCookStack(World world,  BlockPos pos, BlockState state, PlayerEntity player, Direction facing)
    {
        if (!world.isClient)
        {
            // this is legacy support to clear all inventory items that may have been added through the GUI

            ejectAllNotCookStacksToFacing(world,pos, state, player, facing);
        }

        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, state, cookStack, pos, facing);

        furnaceItemStacks[0] = null;
        furnaceItemStacks[1] = null;
        furnaceItemStacks[2] = null;

        setCookStack(null);
    }

    private void ejectAllNotCookStacksToFacing(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction facing)
    {

        if ( furnaceItemStacks[0] != null && !ItemStack.areEqual(furnaceItemStacks[0], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos,state, furnaceItemStacks[0], facing);

            furnaceItemStacks[0] = null;
        }

        if ( furnaceItemStacks[1] != null && !ItemStack.areEqual(furnaceItemStacks[1], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, state, cookStack, facing);

            furnaceItemStacks[1] = null;
        }

        if ( furnaceItemStacks[2] != null && !ItemStack.areEqual(furnaceItemStacks[2], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, state, furnaceItemStacks[2], facing);

            furnaceItemStacks[2] = null;
        }

        markDirty();
    }

    public void setCookStack(ItemStack stack)
    {
        if ( stack != null )
        {
            cookStack = stack.copy();
        }
        else
        {
            cookStack = null;
        }

        if (world != null && !world.isClient) {
            BlockPos pos = getPos();
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
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
        if (bWasBurning && oven.canSmelt() && optional.isPresent()) {
            int cookTime = optional.map(OvenCookingRecipe::getCookTime).orElse(0);
                ++cookTime;

            // Check if the ovenBurnTime is greater than or equal to the cookTime
            if (optional.get().getCookTime() >= cookTime)
            {
                // Consume fuel
                oven.ovenBurnTime -= cookTime;

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
        if (bWasBurning != state.get(LIT)) {
            inventoryChanged = true;
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }

            // Update inventory and visual fuel level
            oven.updateCookStack();
            oven.updateVisualFuelLevel();
        }

        // Notify inventory change if necessary
        if (inventoryChanged) {
            oven.markDirty();
        }
    }


    protected int getCookTimeForCurrentItem()
    {
        return getCookTimeForCurrentItemFromCookStack(this) * cookTimeMultiplier;
    }

    protected int getCookTimeForCurrentItemFromCookStack(BrickOvenBlockEntity oven)
    {
        int iCookTimeShift = 0;

        Optional<OvenCookingRecipe> optional = oven.getRecipeFor(oven.itemBeingCooked.get(0));

        if ( itemBeingCooked.get(0).getItem() != null && optional.isPresent() )
        {
            iCookTimeShift = optional.get().getCookTime();
        }

        return DEFAULT_COOK_TIME << iCookTimeShift;

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

        this.ovenBurnTime = nbt.getShort("BurnTime");

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.itemBeingCooked, true);
        nbt.putIntArray("CookingTimes", this.cookingTimes);
        nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);

        nbt.putShort("BurnTime", (short)this.ovenBurnTime);

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

    public Optional<Integer> getFuelFor(ItemStack stack) {
        // Check if the item is present in the fuelTimeMap
        if (fuelTimeMap.containsKey(stack.getItem())) {
            return Optional.of(fuelTimeMap.get(stack.getItem()));
        } else {
            return Optional.empty();
        }
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
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();

                // Return a copy of the retrieved item
                return cookingStack.copy();
            }
        }

        return ItemStack.EMPTY; // Return an empty stack if no item was retrieved
    }




    public int getFuelTimeForStack(ItemStack stack) {
        Item item = stack.getItem();
        return fuelTimeMap.getOrDefault(item,-1);
    }

    public int attemptToAddFuel(ItemStack stack)
    {
        int iTotalBurnTime = unlitFuelBurnTime + propertyDelegate.get(0);
        int iDeltaBurnTime = maxFuelBurnTime - iTotalBurnTime;
        int iNumItemsBurned = 0;

        if ( iDeltaBurnTime > 0 )
        {
            iNumItemsBurned = iDeltaBurnTime / getItemBurnTime(stack);

            if ( iNumItemsBurned == 0 && getVisualFuelLevel() <= 2 )
            {
                // once the fuel level hits the bottom visual stage, you can jam anything in

                iNumItemsBurned = 1;
            }

            if ( iNumItemsBurned > 0 )
            {
                if ( iNumItemsBurned > stack.getCount() )
                {
                    iNumItemsBurned = stack.getCount();
                }

                unlitFuelBurnTime += getItemBurnTime(stack) * iNumItemsBurned;

                markDirty();
            }
        }

        return iNumItemsBurned;
    }


    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        BrickOvenBlockEntity.addFuel(map, Items.LAVA_BUCKET, 20000);
        BrickOvenBlockEntity.addFuel(map, Blocks.COAL_BLOCK, 16000);
        BrickOvenBlockEntity.addFuel(map, Items.BLAZE_ROD, 2400);
        BrickOvenBlockEntity.addFuel(map, Items.COAL, 1600);
        BrickOvenBlockEntity.addFuel(map, Items.CHARCOAL, 1600);
        BrickOvenBlockEntity.addFuel(map, ItemTags.LOGS, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.BAMBOO_BLOCKS, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.PLANKS, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.BAMBOO_MOSAIC, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_STAIRS, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_SLABS, 150);
        BrickOvenBlockEntity.addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 150);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_FENCES, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.FENCE_GATES, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.NOTE_BLOCK, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.BOOKSHELF, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.LECTERN, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.JUKEBOX, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.CHEST, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.TRAPPED_CHEST, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.CRAFTING_TABLE, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.BANNERS, 300);
        BrickOvenBlockEntity.addFuel(map, Items.BOW, 300);
        BrickOvenBlockEntity.addFuel(map, Items.FISHING_ROD, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.LADDER, 300);
        BrickOvenBlockEntity.addFuel(map, ItemTags.SIGNS, 200);
        BrickOvenBlockEntity.addFuel(map, ItemTags.HANGING_SIGNS, 800);
        BrickOvenBlockEntity.addFuel(map, Items.WOODEN_SHOVEL, 200);
        BrickOvenBlockEntity.addFuel(map, Items.WOODEN_SWORD, 200);
        BrickOvenBlockEntity.addFuel(map, Items.WOODEN_HOE, 200);
        BrickOvenBlockEntity.addFuel(map, Items.WOODEN_AXE, 200);
        BrickOvenBlockEntity.addFuel(map, Items.WOODEN_PICKAXE, 200);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_DOORS, 200);
        BrickOvenBlockEntity.addFuel(map, ItemTags.BOATS, 1200);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOOL, 100);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        BrickOvenBlockEntity.addFuel(map, Items.STICK, 100);
        BrickOvenBlockEntity.addFuel(map, ItemTags.SAPLINGS, 100);
        BrickOvenBlockEntity.addFuel(map, Items.BOWL, 100);
        BrickOvenBlockEntity.addFuel(map, ItemTags.WOOL_CARPETS, 67);
        BrickOvenBlockEntity.addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        BrickOvenBlockEntity.addFuel(map, Items.CROSSBOW, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.BAMBOO, 50);
        BrickOvenBlockEntity.addFuel(map, Blocks.DEAD_BUSH, 100);
        BrickOvenBlockEntity.addFuel(map, Blocks.SCAFFOLDING, 50);
        BrickOvenBlockEntity.addFuel(map, Blocks.LOOM, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.BARREL, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.FLETCHING_TABLE, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.SMITHING_TABLE, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.COMPOSTER, 300);
        BrickOvenBlockEntity.addFuel(map, Blocks.AZALEA, 100);
        BrickOvenBlockEntity.addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        BrickOvenBlockEntity.addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (BrickOvenBlockEntity.isNonFlammableWood(registryEntry.value())) continue;
            fuelTimes.put(registryEntry.value(), fuelTime);
        }
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (BrickOvenBlockEntity.isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        fuelTimes.put(item2, fuelTime);
    }



    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private void updateCookStack()
    {
        ItemStack newCookStack = furnaceItemStacks[0];

        if ( newCookStack == null )
        {
            newCookStack = furnaceItemStacks[2];

            if ( newCookStack == null )
            {
                newCookStack = furnaceItemStacks[1];
            }
        }

        if ( !ItemStack.areEqual(newCookStack, cookStack) )
        {
            setCookStack(newCookStack);
        }
    }


    private void updateVisualFuelLevel()
    {
        int iTotalBurnTime = unlitFuelBurnTime + ovenBurnTime;
        int iNewFuelLevel = 0;

        if ( iTotalBurnTime > 0 )
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


    public void setVisualFuelLevel(int iLevel)
    {
        if (visualFuelLevel != iLevel )
        {
            visualFuelLevel = iLevel;

            world.updateListeners( pos, world.getBlockState(pos), world.getBlockState(pos), 3 );
        }
    }

    public int getItemBurnTime( ItemStack stack )
    {
        return getItemBurnTimeBase( stack ) * brickBurnTimeMultiplier;
    }

    public int getItemBurnTimeBase( ItemStack stack )
    {
        if ( stack != null )
        {
            return ((ItemAdded)stack.getItem()).getOvenBurnTime(stack.getDamage()) * BASE_BURN_TIME_MULTIPLIER;
        }

        return 0;
    }


    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void clear() {
        this.itemBeingCooked.clear();
    }

    public void spawnItemsBeingCooked() {
        if (this.world != null) {
            this.updateListeners();
        }

    }
}
