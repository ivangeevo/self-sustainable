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

public class BrickOvenBE extends BlockEntity implements Ignitable, SingleStackInventory
{
    public int unlitFuelBurnTime;
    public int fuelBurnTime;
    int cookTime = 0;
    int cookTimeTotal = 0;
    private boolean lightOnNextUpdate = false;
    public static final int DEFAULT_COOK_TIME = 400;
    private final int cookTimeMultiplier = 4;
    protected ItemStack cookStack = ItemStack.EMPTY;
    protected ItemStack finishedStack = ItemStack.EMPTY;

    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();

    public final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);
    public static final Map<Item, Integer> FUEL_TIME_MAP = createFuelTimeMap();

    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    public static final int BASE_BURN_TIME_MULTIPLIER = 2;
    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace

    private final int maxFuelBurnTime = ((64 + 7) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier

    private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4);
    private int visualFuelLevel;



    public BrickOvenBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getFirstMatch(new SimpleInventory(stack), this.world);
    }

    private int getCookTimeForCurrentItem(ItemStack stack)
    {
        if (this.getRecipeFor(stack).isPresent())
        {
            return this.getRecipeFor(stack).get().getCookTime();

        }
        return 0;
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


    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBE ovenBE) {
        //setLargeSmokeParticles(world, pos, state);
        setFlameParticles(world, pos, state);


    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public int attemptToAddFuel(ItemStack stack)
    {
        // Check if the item is present in the FUEL_TIME_MAP
        if (!FUEL_TIME_MAP.containsKey(stack.getItem()))
        {
            return 0; // Return 0 to indicate that no items were burned
        }

        int totalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int deltaBurnTime = maxFuelBurnTime - totalBurnTime;
        int numItemsBurned = 0;

        // Get the burn time for the item from the fuel map
        int itemBurnTime = FUEL_TIME_MAP.get(stack.getItem());

        if (deltaBurnTime > 0)
        {
            // Calculate the maximum number of items that can be burned based on fuel ticks
            numItemsBurned = deltaBurnTime / itemBurnTime;

            if (numItemsBurned == 0 && this.getVisualFuelLevel() <= 2)
            {
                // Once the fuel level hits the bottom visual stage, you can jam anything in
                numItemsBurned = 1;
            }

            if (numItemsBurned > 0)
            {
                if (numItemsBurned > stack.getCount())
                {
                    numItemsBurned = stack.getCount();
                }

                // Add the item to the furnace
                unlitFuelBurnTime += itemBurnTime * numItemsBurned;
                markDirty();
            }
        }

        return numItemsBurned;
    }

    public boolean attemptToLight()
    {
        if (unlitFuelBurnTime > 0 )
        {
            // lighting has to be done on update to prevent funkiness with tile entity removal on block being set
            lightOnNextUpdate = true;

            return true;
        }

        return false;
    }

    private void updateVisualFuelLevel()
    {
        int iTotalBurnTime = unlitFuelBurnTime + this.fuelBurnTime;
        int iNewFuelLevel = 0;

        if ( iTotalBurnTime > 0 )
        {
            if (iTotalBurnTime < visualSputterFuelLevel)
            {
                iNewFuelLevel = 1;
            }
            else
            {
                int increments = (iTotalBurnTime - visualSputterFuelLevel) / visualFuelLevelIncrement;
                iNewFuelLevel = Math.min(increments + 2, 8);
            }
        }

        setVisualFuelLevel(iNewFuelLevel);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (isNonFlammableWood(registryEntry.value())) continue;
            fuelTimes.put(registryEntry.value(), fuelTime);
        }
    }
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        fuelTimes.put(item2, fuelTime);
    }

    /**
     * {@return whether the provided {@code item} is in the {@link
     * ItemTags#NON_FLAMMABLE_WOOD non_flammable_wood} tag}
     */
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        addFuel(map, Blocks.COAL_BLOCK, 14400);
        addFuel(map, Items.BLAZE_ROD, 12800);
        
        // Logs
        addFuel(map, Items.BIRCH_LOG, 16000);
        addFuel(map, Items.ACACIA_LOG, 16000);
        addFuel(map, Items.OAK_LOG, 12800);
        addFuel(map, Items.DARK_OAK_LOG, 12800);
        addFuel(map, Items.CHERRY_LOG, 12800);
        addFuel(map, Items.SPRUCE_LOG, 9600);
        addFuel(map, Items.MANGROVE_LOG, 8400);
        addFuel(map, Items.JUNGLE_LOG, 6400);
        addFuel(map, ItemTags.BAMBOO_BLOCKS, 500);

        // Planks
        addFuel(map, Items.BIRCH_PLANKS, 16000);
        addFuel(map, Items.ACACIA_PLANKS, 16000);
        addFuel(map, Items.OAK_PLANKS, 12800);
        addFuel(map, Items.DARK_OAK_PLANKS, 12800);
        addFuel(map, Items.CHERRY_PLANKS, 12800);
        addFuel(map, Items.SPRUCE_PLANKS, 9600);
        addFuel(map, Items.MANGROVE_PLANKS, 8400);
        addFuel(map, Items.JUNGLE_PLANKS, 6400);
        addFuel(map, Items.BAMBOO_PLANKS, 40);

        // Wooden Stairs
        addFuel(map, Items.BIRCH_STAIRS, 400);
        addFuel(map, Items.ACACIA_STAIRS, 400);
        addFuel(map, Items.OAK_STAIRS, 300);
        addFuel(map, Items.DARK_OAK_STAIRS, 300);
        addFuel(map, Items.CHERRY_STAIRS, 300);
        addFuel(map, Items.SPRUCE_STAIRS, 200);
        addFuel(map, Items.MANGROVE_STAIRS, 200);
        addFuel(map, Items.JUNGLE_STAIRS, 70);
        addFuel(map, Items.BAMBOO_STAIRS, 30);


        addFuel(map, Blocks.BAMBOO_MOSAIC, 40);
        addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 30);
        addFuel(map, ItemTags.WOODEN_SLABS, 150);
        addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 20);
        addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map, ItemTags.WOODEN_FENCES, 300);
        addFuel(map, ItemTags.FENCE_GATES, 300);
        addFuel(map, Blocks.NOTE_BLOCK, 300);
        addFuel(map, Blocks.BOOKSHELF, 300);
        addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map, Blocks.LECTERN, 300);
        addFuel(map, Blocks.JUKEBOX, 300);
        addFuel(map, Blocks.CHEST, 300);
        addFuel(map, Blocks.TRAPPED_CHEST, 300);
        addFuel(map, Blocks.CRAFTING_TABLE, 300);
        addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map, ItemTags.BANNERS, 300);
        addFuel(map, Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map, Blocks.LADDER, 300);
        addFuel(map, ItemTags.SIGNS, 200);
        addFuel(map, ItemTags.HANGING_SIGNS, 800);
        addFuel(map, Items.WOODEN_SHOVEL, 200);
        addFuel(map, Items.WOODEN_SWORD, 200);
        addFuel(map, Items.WOODEN_HOE, 200);
        addFuel(map, Items.WOODEN_AXE, 200);
        addFuel(map, Items.WOODEN_PICKAXE, 200);
        addFuel(map, ItemTags.WOODEN_DOORS, 200);
        addFuel(map, ItemTags.BOATS, 1200);
        addFuel(map, ItemTags.WOOL, 100);
        addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map, Items.STICK, 50);
        addFuel(map, ItemTags.SAPLINGS, 100);
        addFuel(map, Items.BOWL, 100);
        addFuel(map, ItemTags.WOOL_CARPETS, 67);
        addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map, Items.CROSSBOW, 300);
        addFuel(map, Blocks.BAMBOO, 50);
        addFuel(map, Blocks.DEAD_BUSH, 100);
        addFuel(map, Blocks.SCAFFOLDING, 50);
        addFuel(map, Blocks.LOOM, 300);
        addFuel(map, Blocks.BARREL, 300);
        addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map, Blocks.FLETCHING_TABLE, 300);
        addFuel(map, Blocks.SMITHING_TABLE, 300);
        addFuel(map, Blocks.COMPOSTER, 300);
        addFuel(map, Blocks.AZALEA, 100);
        addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    private static void setLargeSmokeParticles(World world, BlockPos pos, BlockState state)
    {
        Random random = world.random;

        boolean hasItemToCook = false;

        for (Direction direction : Direction.Type.HORIZONTAL)
        {

            if (state.get(LIT) && !hasItemToCook && random.nextFloat() < 0.2f)
            {
                double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.25f)
                        + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125f);
                double e = Math.max(pos.getY() + 0.5, Math.min(pos.getY() + 1.0, (double) pos.getY() + 0.7));
                double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.25f)
                        + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125f);

                for (int k = 0; k < 4; ++k) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                }

                hasItemToCook = true; // Set the boolean to true to avoid spawning more than one set of particles
            }
        }
    }

    private static void setFlameParticles(World world, BlockPos pos, BlockState state) {
        if (!state.get(LIT))
        {
            return;
        }

        double d = (double) pos.getX() + 0.5;
        double e = pos.getY();
        double f = (double) pos.getZ() + 0.5;

        if (world.getRandom().nextDouble() < 0.05)
        {
            world.playSound(d, e, f, SoundEvents.BLOCK_FIRE_AMBIENT,
                    SoundCategory.BLOCKS, 0.25F + world.random.nextFloat() * 0.25F,
                    0.5F + world.random.nextFloat() * 0.25F, false );
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

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cookStack = ItemStack.EMPTY;
        this.visualFuelLevel = nbt.getInt("VisualFuelLevel");

        if (nbt.contains("CookStack"))
        {
            cookStack = ItemStack.fromNbt(nbt.getCompound("CookStack"));
        }
        this.unlitFuelBurnTime = nbt.getShort("UnlitFuelBurnTime");
        this.fuelBurnTime = nbt.getShort("FuelBurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");

        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putShort("VisualFuelLevel", (short) this.visualFuelLevel);
        nbt.putShort("UnlitFuelBurnTime", (short) this.unlitFuelBurnTime);
        nbt.putShort("FuelBurnTime", (short) this.fuelBurnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        this.writeCookStackNbt(nbt, this.cookStack);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }

    private void writeCookStackNbt(NbtCompound nbt, ItemStack stack) {
        if (!stack.isEmpty()) {
            NbtCompound stackNbt = new NbtCompound();
            stack.writeNbt(stackNbt);
            nbt.put("CookStack", stackNbt);
        } else {
            nbt.put("CookStack", new NbtCompound()); // Empty compound to indicate empty ItemStack
        }
    }


    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public int getVisualFuelLevel() {
        return visualFuelLevel;
    }

    private boolean isBurning() {
        return this.fuelBurnTime > 0;
    }

    public void setVisualFuelLevel(int visualFuelLevel) {
        assert this.world != null;
        this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(BrickOvenBlock.FUEL_LEVEL, visualFuelLevel), Block.NOTIFY_ALL);
        this.visualFuelLevel = visualFuelLevel;
        markDirty();  // Mark the block entity as changed
    }

    public boolean addItem(Entity user, ItemStack stack, int cookTime)
    {

            this.cookTimeTotal = cookTime;
            this.cookTime = 0;
            this.cookStack = stack.split(1);
            this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners();

            return true;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void retrieveItem(World world, PlayerEntity player)
    {
        ItemStack cookStack = getStack();

        if (!cookStack.isEmpty() && !world.isClient())
        {
            if (!getStack().isEmpty())
            {
                player.giveItemStack(getStack());
                setStack(ItemStack.EMPTY);
                markDirty();
                Objects.requireNonNull(this.getWorld()).updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return cookStack.isEmpty();
    }
    @Override
    public ItemStack getStack() {
        return cookStack;
    }
    @Override
    public ItemStack removeStack()
    {
        return cookStack = ItemStack.EMPTY;
    }
    @Override
    public void setStack(ItemStack newStack)
    {
        cookStack = newStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        writeCookStackNbt(nbtCompound, this.cookStack);
        return nbtCompound;
    }


    @Override
    public void clear() {

    }
}
