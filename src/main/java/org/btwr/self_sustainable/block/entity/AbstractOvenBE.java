package org.btwr.self_sustainable.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.util.TickableBlockEntity;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractOvenBE extends BlockEntity implements IgnitableBlock, TickableBlockEntity {

    public static final Map<Item, Integer> FUEL_TIME_MAP = AbstractFurnaceBlockEntity.createFuelTimeMap();
    public static final int BASE_BURN_TIME_MULTIPLIER = 2;
    public static final int DEFAULT_COOK_TIME = 400;

    protected final int cookTimeMultiplier = 4;

    protected int unlitFuelBurnTime;
    protected int fuelBurnTime;

    protected int cookTime = 0;
    protected int cookTimeTotal = 0;

    protected int visualFuelLevel;

    protected boolean lightOnNextUpdate = false;
    public boolean mortarOnNextUpdate = false;

    // The fuel values are the same as vanilla's furnace map to maintain compatability with other mods.
    final int brickBurnTimeMultiplier = 4; // applied on top of the base multiplier of standard furnace
    final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
    final RecipeManager.MatchGetter<SingleStackRecipeInput, OvenCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);

    public final int maxFuelBurnTime = ((64 + 7) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier
    public final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    public final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4);

    protected final float CHANCE_OF_FIRE_SPREAD = 0.01F;

    public AbstractOvenBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public Optional<RecipeEntry<OvenCookingRecipe>> getRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        return this.matchGetter.getFirstMatch(new SingleStackRecipeInput(stack), this.world);
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public boolean attemptToLight() {
        if (unlitFuelBurnTime > 0) {
            // lighting has to be done on update to prevent funkiness with tile entity removal on block being set
            lightOnNextUpdate = true;
            return true;
        }

        return false;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        // Furnace base
        this.fuelBurnTime = nbt.getShort("FuelBurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");

        // FCMOD: Code added to track extended burn times
        if (nbt.contains("ExtendedBurnTime")) {
            fuelBurnTime = nbt.getInt("ExtendedBurnTime");
            cookTime = nbt.getInt("ExtendedCookTime");

            if (nbt.contains("ExtendedItemBurnTime")) {
                int newItemBurnTime = nbt.getInt("ExtendedItemBurnTime");
                setCookTimeForCurrentItem(newItemBurnTime);
            }
        }

        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(Identifier.of(string), nbtCompound.getInt(string));
        }

        // Oven added
        this.visualFuelLevel = nbt.getInt("VisualFuelLevel");
        this.unlitFuelBurnTime = nbt.getInt("UnlitFuelBurnTime");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        // Furnace base
        nbt.putShort("FuelBurnTime", (short) this.fuelBurnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);

        // FCMOD: Code added to track extended burn times
        nbt.putInt("ExtendedBurnTime", fuelBurnTime);
        nbt.putInt("ExtendedCookTime", cookTime);
        nbt.putInt("ExtendedItemBurnTime", fuelBurnTime);

        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);

        // Oven added
        nbt.putInt("UnlitFuelBurnTime", this.unlitFuelBurnTime);
        nbt.putByte("VisualFuelLevel", (byte) this.visualFuelLevel);
    }

    protected int getItemBurnTime(ItemStack stack) {
        return FUEL_TIME_MAP.get(stack.getItem()) * brickBurnTimeMultiplier;
    }

    protected int getCookTimeForCurrentItem() {
        return cookTimeTotal * cookTimeMultiplier;
    }

    protected void setCookTimeForCurrentItem(int cookTime) {
        cookTimeTotal = cookTime;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    boolean isBurning() {
        return this.fuelBurnTime > 0;
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

                if (world != null) {
                    markDirty(world, this.getPos(), this.getCachedState());
                }
            }
        }

        return numItemsBurned;
    }

    protected void updateVisualFuelLevel() {
        int totalBurnTime = unlitFuelBurnTime + fuelBurnTime;
        int newFuelLevel = 0;

        if (totalBurnTime > 0) {
            if (totalBurnTime < visualSputterFuelLevel) {
                newFuelLevel = 1;
            }
            else {
                newFuelLevel = (totalBurnTime / visualFuelLevelIncrement) + 2;
            }
        }

        setVisualFuelLevel(newFuelLevel);
    }

    public int getVisualFuelLevel() {
        return visualFuelLevel;
    }

    public void setVisualFuelLevel(int level) {
        if (world == null) return;

        if (visualFuelLevel != level) {
            visualFuelLevel = level;

            if (!world.isClient) {
                BlockState current = getCachedState();

                // Replaces: MathHelper.clamp_int(iFuelLevel - 2, 0, 8)
                // Levels 0, 1, 2 all show no fuel overlay (FUEL_LEVEL 0)
                // Levels 3+ map to FUEL_LEVEL 1-8
                int blockStateFuelLevel = Math.max(0, Math.min(level - 2, 8));

                world.setBlockState(pos,
                        current.with(BrickOvenBlock.FUEL_LEVEL, blockStateFuelLevel),
                        Block.NOTIFY_ALL);
            }

            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public static int calcRedstoneFromOven(BrickOvenBE be) {
        return be.getCookStack() != null ? 15 : 0;
    }

}