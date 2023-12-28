package ivangeevo.selfsustainable.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.ivangeevo.btwr.core.item.BTWR_Items;
import net.ivangeevo.btwr.core.tag.BTWRTags;
import net.ivangeevo.btwr.core.util.ItemUtils;
import net.minecraft.SharedConstants;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractOvenBlockEntity extends LockableContainerBlockEntity implements SidedInventory, RecipeUnlocker, RecipeInputProvider {
    protected static final int INPUT_SLOT_INDEX = 0;
    protected static final int FUEL_SLOT_INDEX = 1;
    protected static final int OUTPUT_SLOT_INDEX = 2;
    public static final int BURN_TIME_PROPERTY_INDEX = 0;
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    public static final int FUEL_TIME_PROPERTY_INDEX = 1;
    public static final int COOK_TIME_PROPERTY_INDEX = 2;
    public static final int COOK_TIME_TOTAL_PROPERTY_INDEX = 3;
    public static final int PROPERTY_COUNT = 4;
    public static final int DEFAULT_COOK_TIME = 200;
    public static final int field_31295 = 2;
    protected DefaultedList<ItemStack> inventory;
    int burnTime;
    int fuelTime;
    int cookTime;
    int cookTimeTotal;

    private int unlitFuelBurnTime = 0;
    private int visualFuelLevel = 0;

    private final int cookTimeMultiplier = 4;


    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace


    private ItemStack cookStack = null;

    protected final PropertyDelegate propertyDelegate;
    private final Object2IntOpenHashMap<Identifier> recipesUsed;
    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;

    protected ItemStack[] furnaceItemStacks = new ItemStack[3];


    protected AbstractOvenBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType, pos, state);
        this.inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
        this.propertyDelegate = new PropertyDelegate() {
            public int get(int index) {
                return switch (index) {
                    case 0 -> AbstractOvenBlockEntity.this.burnTime;
                    case 1 -> AbstractOvenBlockEntity.this.fuelTime;
                    case 2 -> AbstractOvenBlockEntity.this.cookTime;
                    case 3 -> AbstractOvenBlockEntity.this.cookTimeTotal;
                    default -> 0;
                };
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0 -> AbstractOvenBlockEntity.this.burnTime = value;
                    case 1 -> AbstractOvenBlockEntity.this.fuelTime = value;
                    case 2 -> AbstractOvenBlockEntity.this.cookTime = value;
                    case 3 -> AbstractOvenBlockEntity.this.cookTimeTotal = value;
                }

            }

            public int size() {
                return 4;
            }
        };
        this.recipesUsed = new Object2IntOpenHashMap<>();
        this.matchGetter = RecipeManager.createCachedMatchGetter(recipeType);
        this.markDirty();
    }




    public static Map<Item, Integer> createFuelTimeMap() {
        Map<Item, Integer> map = Maps.newLinkedHashMap();
        addFuel(map,  Items.BLAZE_ROD, 44000);

        addFuel(map, BTWRTags.Items.MEDIUM_VALUE_FUELS,500);
        addFuel(map,  Items.COAL, 15000);
        addFuel(map,  Items.CHARCOAL, 14500);
        addFuel(map,  ItemTags.LOGS, 1200);
        addFuel(map,  ItemTags.PLANKS, 320);
        addFuel(map,  ItemTags.WOODEN_STAIRS, 300);
        addFuel(map,  ItemTags.WOODEN_SLABS, 150);
        addFuel(map,  ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map,  ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map,  Blocks.OAK_FENCE, 300);
        addFuel(map,  Blocks.BIRCH_FENCE, 300);
        addFuel(map,  Blocks.SPRUCE_FENCE, 300);
        addFuel(map,  Blocks.JUNGLE_FENCE, 300);
        addFuel(map,  Blocks.DARK_OAK_FENCE, 300);
        addFuel(map,  Blocks.ACACIA_FENCE, 300);
        addFuel(map,  Blocks.MANGROVE_FENCE, 300);
        addFuel(map,  Blocks.OAK_FENCE_GATE, 300);
        addFuel(map,  Blocks.BIRCH_FENCE_GATE, 300);
        addFuel(map,  Blocks.SPRUCE_FENCE_GATE, 300);
        addFuel(map,  Blocks.JUNGLE_FENCE_GATE, 300);
        addFuel(map,  Blocks.DARK_OAK_FENCE_GATE, 300);
        addFuel(map,  Blocks.ACACIA_FENCE_GATE, 300);
        addFuel(map,  Blocks.MANGROVE_FENCE_GATE, 300);
        addFuel(map,  Blocks.NOTE_BLOCK, 300);
        addFuel(map,  Blocks.BOOKSHELF, 300);
        addFuel(map,  Blocks.LECTERN, 300);
        addFuel(map,  Blocks.JUKEBOX, 300);
        addFuel(map,  Blocks.CHEST, 300);
        addFuel(map,  Blocks.TRAPPED_CHEST, 300);
        addFuel(map,  Blocks.CRAFTING_TABLE, 300);
        addFuel(map,  Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map,  ItemTags.BANNERS, 300);
        addFuel(map,  Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map,  Blocks.LADDER, 300);
        addFuel(map,  ItemTags.SIGNS, 200);
        addFuel(map,  Items.WOODEN_SHOVEL, 200);
        addFuel(map,  Items.WOODEN_SWORD, 200);
        addFuel(map,  Items.WOODEN_HOE, 200);
        addFuel(map,  Items.WOODEN_AXE, 200);
        addFuel(map,  Items.WOODEN_PICKAXE, 200);
        addFuel(map,  ItemTags.WOODEN_DOORS, 200);
        addFuel(map,  ItemTags.BOATS, 1200);
        addFuel(map,  ItemTags.WOOL, 100);
        addFuel(map,  ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map,  Items.STICK, 250);
        addFuel(map,  ItemTags.SAPLINGS, 100);
        addFuel(map,  Items.BOWL, 100);
        addFuel(map,  ItemTags.WOOL_CARPETS, 67);
        addFuel(map,  Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map,  Items.CROSSBOW, 300);
        addFuel(map,  Blocks.BAMBOO, 50);
        addFuel(map,  Blocks.DEAD_BUSH, 100);
        addFuel(map,  Blocks.SCAFFOLDING, 50);
        addFuel(map,  Blocks.LOOM, 300);
        addFuel(map,  Blocks.BARREL, 300);
        addFuel(map,  Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map,  Blocks.FLETCHING_TABLE, 300);
        addFuel(map,  Blocks.SMITHING_TABLE, 300);
        addFuel(map,  Blocks.COMPOSTER, 300);
        addFuel(map,  Blocks.AZALEA, 100);
        addFuel(map,  Blocks.FLOWERING_AZALEA, 100);
        addFuel(map,  Blocks.MANGROVE_ROOTS, 300);

        // MODDED ITEMS
        



        addFuel(map,BTWR_Items.DUST_COAL, 6500);
        addFuel(map, BTWRTags.Items.MEDIUM_VALUE_FUELS , 500);
        addFuel(map, BTWRTags.Items.LOW_VALUE_FUELS , 200);


        addFuel(map,BTWR_Items.CHISEL_WOOD, 750);





        return map;
    }

    /**
     * {@return whether the provided {@code item} is in the {@link
     * ItemTags#NON_FLAMMABLE_WOOD non_flammable_wood} tag}
     */
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {

        for (RegistryEntry<Item> itemRegistryEntry : Registry.ITEM.iterateEntries(tag)) {
            if (!isNonFlammableWood((Item) ((RegistryEntry<?>) itemRegistryEntry).value())) {
                fuelTimes.put((Item) ((RegistryEntry<?>) itemRegistryEntry).value(), fuelTime);
            }
        }

    }

    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
        } else {
            fuelTimes.put(item2, fuelTime);
        }
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readNbt(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");

        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.writeNbt(nbt, this.inventory);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> {
            nbtCompound.putInt(identifier.toString(), count);
        });
        nbt.put("RecipesUsed", nbtCompound);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractOvenBlockEntity blockEntity) {
        boolean bl = blockEntity.isBurning();
        boolean bl2 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
        }

        ItemStack itemStack = blockEntity.inventory.get(1);
        boolean bl3 = !blockEntity.inventory.get(0).isEmpty();
        boolean bl4 = !itemStack.isEmpty();
        if (blockEntity.isBurning() || bl4 && bl3) {
            Recipe recipe;
            if (bl3) {
                recipe = blockEntity.matchGetter.getFirstMatch(blockEntity, world).orElse(null);
            } else {
                recipe = null;
            }

            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.burnTime = blockEntity.getFuelTime(itemStack);
                blockEntity.fuelTime = blockEntity.burnTime;
                if (blockEntity.isBurning()) {
                    bl2 = true;
                    if (bl4) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            blockEntity.inventory.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }

            if (blockEntity.isBurning() && canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.cookTime;
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    blockEntity.cookTimeTotal = getCookTime(world, blockEntity);
                    if (craftRecipe(recipe, blockEntity.inventory, i)) {
                        blockEntity.setLastRecipe(recipe);
                    }

                    bl2 = true;
                }
            } else {
                blockEntity.cookTime = 0;
            }
        } else if (!blockEntity.isBurning() && blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }

        if (bl != blockEntity.isBurning()) {
            bl2 = true;
            state = state.with(AbstractFurnaceBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }

        if (bl2) {
            markDirty(world, pos, state);
        }

    }

    private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (!slots.get(0).isEmpty() && recipe != null) {
            ItemStack itemStack = recipe.getOutput();
            if (itemStack.isEmpty()) {
                return false;
            } else {
                ItemStack itemStack2 = slots.get(2);
                if (itemStack2.isEmpty()) {
                    return true;
                } else if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
                    return false;
                } else if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
                    return true;
                } else {
                    return itemStack2.getCount() < itemStack.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private static boolean craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe != null && canAcceptRecipeOutput(recipe, slots, count)) {
            ItemStack itemStack = slots.get(0);
            ItemStack itemStack2 = recipe.getOutput();
            ItemStack itemStack3 = slots.get(2);
            if (itemStack3.isEmpty()) {
                slots.set(2, itemStack2.copy());
            } else if (itemStack3.isOf(itemStack2.getItem())) {
                itemStack3.increment(1);
            }

            if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !slots.get(1).isEmpty() && slots.get(1).isOf(Items.BUCKET)) {
                slots.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemStack.decrement(1);
            return true;
        } else {
            return false;
        }
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            Item item = fuel.getItem();
            return createFuelTimeMap().getOrDefault(item, 0);
        }
    }

    private static int getCookTime(World world, AbstractOvenBlockEntity furnace) {
        return furnace.matchGetter.getFirstMatch(furnace, world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return createFuelTimeMap().containsKey(stack.getItem());
    }

    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else {
            return side == Direction.UP ? TOP_SLOTS : SIDE_SLOTS;
        }
    }

    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 1) {
            return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
        } else {
            return true;
        }
    }

    public int size() {
        return this.inventory.size();
    }

    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.inventory.iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }



    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }

        if (slot == 0 && !bl) {
            this.cookTimeTotal = getCookTime(this.world, this);
            this.cookTime = 0;
            this.markDirty();
        }

    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
        }
    }

    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 2) {
            return false;
        } else if (slot != 1) {
            return true;
        } else {
            ItemStack itemStack = this.inventory.get(1);
            return canUseAsFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
        }
    }

    public void clear() {
        this.inventory.clear();
    }

    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier identifier = recipe.getId();
            this.recipesUsed.addTo(identifier, 1);
        }

    }

    @Nullable
    public Recipe<?> getLastRecipe() {
        return null;
    }

    public void unlockLastRecipe(PlayerEntity player) {
    }

    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        List<Recipe<?>> list = this.getRecipesUsedAndDropExperience(player.getWorld(), player.getPos());
        player.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        List<Recipe<?>> list = Lists.newArrayList();
        ObjectIterator var4 = this.recipesUsed.object2IntEntrySet().iterator();

        while(var4.hasNext()) {
            Object2IntMap.Entry<Identifier> entry = (Object2IntMap.Entry)var4.next();
            world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                dropExperience(world, pos, entry.getIntValue(), ((AbstractCookingRecipe)recipe).getExperience());
            });
        }

        return list;
    }

    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        int i = MathHelper.floor((float)multiplier * experience);
        float f = MathHelper.fractionalPart((float)multiplier * experience);
        if (f != 0.0F && Math.random() < (double)f) {
            ++i;
        }

        ExperienceOrbEntity.spawn(world, pos, i);
    }

    public void provideRecipeInputs(RecipeMatcher finder) {
        Iterator var2 = this.inventory.iterator();

        while(var2.hasNext()) {
            ItemStack itemStack = (ItemStack)var2.next();
            finder.addInput(itemStack);
        }

    }

    /** **/
    /** **/
    /** **/

    /** BTW ADDED METHOD // TO BE REVISED **/

    /** **/
    /** **/
    /** **/

    public ItemStack getCookStack () {
        return cookStack;
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

        assert world != null;
        world.markDirty(pos);
    }

    public void givePlayerCookStack(PlayerEntity player, int iFacing)
    {
        assert world != null;
        if ( !player.getWorld().isClient) {
            // this is legacy support to clear all inventory items that may have been added through the GUI

            ejectAllNotCookStacksToFacing(player, iFacing);
        }

        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, cookStack, pos, iFacing);

        furnaceItemStacks[0] = null;
        furnaceItemStacks[1] = null;
        furnaceItemStacks[2] = null;


        setCookStack(null);
    }

    private void ejectAllNotCookStacksToFacing(PlayerEntity player, int iFacing)
    {
        if ( furnaceItemStacks[0] != null && !ItemStack.areEqual(furnaceItemStacks[0], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, furnaceItemStacks[0], iFacing);

            furnaceItemStacks[0] = null;
        }

        if ( furnaceItemStacks[1] != null && !ItemStack.areEqual(furnaceItemStacks[1], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, furnaceItemStacks[1], iFacing);

            furnaceItemStacks[1] = null;
        }

        if ( furnaceItemStacks[2] != null && !ItemStack.areEqual(furnaceItemStacks[2], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, furnaceItemStacks[2], iFacing);

            furnaceItemStacks[2] = null;
        }

        markDirty();
    }

    public void addCookStack(ItemStack stack)
    {
        furnaceItemStacks[0] = stack;

        markDirty();

    }


    public int furnaceBurnTime = FurnaceBlockEntity.BURN_TIME_PROPERTY_INDEX;

    // the following is not the actual maximum time, but rather the point above which additional fuel can no longer be added
    //private final int m_iMaxFuelBurnTime = ( 1600 * 2 * 2 ); // 1600 oak log burn time, 2x base furnace multiplier, 2x brick furnace multiplier
    // the following is an actual max
    private final int maxFuelBurnTime = ((64 + 7 ) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier

    private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4 );


    public int attemptToAddFuel(ItemStack stack) {

        int iTotalBurnTime = unlitFuelBurnTime + furnaceBurnTime;
        int iDeltaBurnTime = maxFuelBurnTime - iTotalBurnTime;
        int iNumItemsBurned = 0;

        if ( iDeltaBurnTime > 0 )
        {
            iNumItemsBurned = iDeltaBurnTime / this.getFuelTime(stack);

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

                unlitFuelBurnTime += getFuelTime(stack) * iNumItemsBurned;

                markDirty();
            }
        }

        return iNumItemsBurned;
    }

    private void updateVisualFuelLevel()
    {
        int iTotalBurnTime = unlitFuelBurnTime + furnaceBurnTime;
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

            assert world != null;
            world.markDirty(pos);
        }
    }

}
