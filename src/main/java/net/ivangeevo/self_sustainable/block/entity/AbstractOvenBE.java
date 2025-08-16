package net.ivangeevo.self_sustainable.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.ivangeevo.self_sustainable.block.blocks.SmokerOvenBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.util.CustomSingleStackInventory;
import net.ivangeevo.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractOvenBE extends BlockEntity implements Ignitable, CustomSingleStackInventory {

    public int unlitFuelBurnTime;
    public int fuelBurnTime;
    int cookTime = 0;
    int cookTimeTotal = 0;
    public boolean lightOnNextUpdate = false;
    public static final int DEFAULT_COOK_TIME = 400;
    private final int cookTimeMultiplier = 4;
    protected ItemStack cookStack = ItemStack.EMPTY;
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    final RecipeManager.MatchGetter<SingleStackRecipeInput, OvenCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);
    // The fuel values are the same as vanilla's furnace map to maintain compatability with other mods.
    public static final Map<Item, Integer> FUEL_TIME_MAP = AbstractFurnaceBlockEntity.createFuelTimeMap();
    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    public static final int BASE_BURN_TIME_MULTIPLIER = 2;
    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace
    public final int maxFuelBurnTime = ((64 + 7) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier
    public final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    public final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4);
    protected int visualFuelLevel;

    public AbstractOvenBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.cookStack = ItemStack.EMPTY;
    }

    public Optional<RecipeEntry<OvenCookingRecipe>> getRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getFirstMatch(new SingleStackRecipeInput(stack), this.world);
    }


    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public int attemptToAddFuel(ItemStack stack) {
        // Check if the item is present in the FUEL_TIME_MAP
        if (!FUEL_TIME_MAP.containsKey(stack.getItem())) {
            return 0; // Return 0 to indicate that no items were burned
        }

        int totalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int deltaBurnTime = maxFuelBurnTime - totalBurnTime;
        int numItemsBurned = 0;

        // Get the burn time for the item from the fuel map
        int itemBurnTime = FUEL_TIME_MAP.get(stack.getItem());

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

    public boolean attemptToLight() {
        if (unlitFuelBurnTime > 0) {
            // lighting has to be done on update to prevent funkiness with tile entity removal on block being set
            lightOnNextUpdate = true;
            return true;
        }

        return false;
    }

    void updateVisualFuelLevel() {
        int iTotalBurnTime = unlitFuelBurnTime + this.fuelBurnTime;
        int iNewFuelLevel = 0;

        if (iTotalBurnTime > 0) {
            if (iTotalBurnTime < visualSputterFuelLevel) {
                iNewFuelLevel = 1;
            } else {
                int increments = (iTotalBurnTime - visualSputterFuelLevel) / visualFuelLevelIncrement;
                iNewFuelLevel = Math.min(increments + 2, 8);
            }
        }

        setVisualFuelLevel(iNewFuelLevel);
    }

    static void setParticles(World world, BlockPos pos, BlockState state) {
        if (!state.get(LIT)) return;

        double d = (double) pos.getX() + 0.5;
        double e = pos.getY();
        double f = (double) pos.getZ() + 0.5;

        if (world.getRandom().nextDouble() < 0.05) {
            world.playSound(d, e, f, SoundEvents.BLOCK_FIRE_AMBIENT,
                    SoundCategory.BLOCKS, 0.25F + world.random.nextFloat() * 0.25F,
                    0.5F + world.random.nextFloat() * 0.25F, false );
            Direction direction = state.get(SmokerOvenBlock.FACING);
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
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.visualFuelLevel = nbt.getInt("VisualFuelLevel");
        this.unlitFuelBurnTime = nbt.getShort("UnlitFuelBurnTime");
        this.fuelBurnTime = nbt.getShort("FuelBurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.readCookStackNbt(nbt, registryLookup);

        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(Identifier.of(string), nbtCompound.getInt(string));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putShort("VisualFuelLevel", (short) this.visualFuelLevel);
        nbt.putShort("UnlitFuelBurnTime", (short) this.unlitFuelBurnTime);
        nbt.putShort("FuelBurnTime", (short) this.fuelBurnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        this.writeCookStackNbt(nbt, this.cookStack, registryLookup);

        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }

    private void readCookStackNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (!nbt.contains("CookStack", 10)) return;
        cookStack = ItemStack.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), nbt.getCompound("CookStack"))
                .result()
                .orElse(ItemStack.EMPTY);
    }

    private void writeCookStackNbt(NbtCompound nbt, ItemStack cookStack, RegistryWrapper.WrapperLookup registryLookup) {
        if (cookStack.isEmpty()) return;
        nbt.put("CookStack", cookStack.encode(registryLookup, new NbtCompound()));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = createNbt(registryLookup);
        this.writeCookStackNbt(nbtCompound, this.cookStack, registryLookup);
        return nbtCompound;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public int getVisualFuelLevel() {
        return visualFuelLevel;
    }

    boolean isBurning() {
        return this.fuelBurnTime > 0;
    }

    public void setVisualFuelLevel(int visualFuelLevel) {
        assert this.world != null;
        this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(SmokerOvenBlock.FUEL_LEVEL, visualFuelLevel), Block.NOTIFY_ALL);
        this.visualFuelLevel = visualFuelLevel;
        markDirty();
    }

    public boolean addItem(Entity user, ItemStack stack, int cookTime) {
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

    //TODO: Check if this isn't causing problems/potential exploits because it' doesn't check the logical side it's running on like the addItem() does.
    public void retrieveItem(PlayerEntity player) {
        ItemStack cookStack = getCookStack();

        boolean addedToInventory = player.giveItemStack(cookStack);
        if (!addedToInventory) {
            player.dropItem(cookStack, false);
        }
        this.setStack(ItemStack.EMPTY);
        this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(player, this.getCachedState()));
        this.updateListeners();
    }



    @Override
    public boolean isEmpty() {
        return cookStack.isEmpty();
    }

    @Override
    public ItemStack getCookStack() {
        return cookStack;
    }

    @Override
    public ItemStack removeStack() {
        return cookStack = ItemStack.EMPTY;
    }

    @Override
    public void setStack(ItemStack newStack) {
        cookStack = newStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }

    @Override
    public void clear() {}

}
