package org.btwr.self_sustainable.block.entity;

import org.btwr.self_sustainable.block.interfaces.added.CampfireBlockAdded;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.block.utils.CampfireState;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import org.btwr.self_sustainable.util.MiscUtils;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Clearable;
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

import static org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;
import static org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock.FUEL_STATE;

public class VariableCampfireBE extends BlockEntity implements Clearable {

    // TODO: Change to a singular itemstack, since this campfire only holds one food item
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int cookingTime = 0;
    private int cookingTotalTime = 0;
    private final RecipeManager.MatchGetter<SingleStackRecipeInput, CampfireCookingRecipe> matchGetter =
            RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

    // BTW Added variables
    private static final int BASE_BURN_TIME_MULTIPLIER = 2;

    private static final int CAMPFIRE_BURN_TIME_MULTIPLIER = 8;

    private static final int DEFAULT_COOK_TIME = 400;

    // this variable represents efficiency relative to furnace cooking
    private static final int TIME_TO_COOK = (DEFAULT_COOK_TIME * CAMPFIRE_BURN_TIME_MULTIPLIER * 3 / 2 );

    private static final int MAX_BURN_TIME = (5 * MiscUtils.TICKS_PER_MINUTE);

    // 50 is the furnace burn time of a shaft
    private static final int INITIAL_BURN_TIME = (50 * 4 * CAMPFIRE_BURN_TIME_MULTIPLIER * BASE_BURN_TIME_MULTIPLIER);

    private static final int WARMUP_TIME = (10 * MiscUtils.TICKS_PER_SECOND);

    private static final int REVERT_TO_SMALL_TIME = (20 * MiscUtils.TICKS_PER_SECOND);

    private static final int BLAZE_TIME = (INITIAL_BURN_TIME * 3 / 2 );

    // used to be 2 minutes
    private static final int SMOULDER_TIME = (5 * MiscUtils.TICKS_PER_MINUTE);

    private static final int TIME_TO_BURN_FOOD = (TIME_TO_COOK / 2 );

    private static final float CHANCE_OF_FIRE_SPREAD = 0.05F;

    // custom set chance because we don't take into account the fire chance spreading values that btw applies from the fire block logic
    private static final float MODIFIED_CHANCE_OF_FIRE_SPREAD = 0.0045F;

    private static final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.01F;

    private ItemStack spitStack = null;

    private int burnTimeCountdown = 0;
    private int burnTimeSinceLit = 0;
    private int cookCounter = 0;
    private int smoulderCounter = 0;
    private int cookBurningCounter = 0;

    public int getCookTime() {
        return cookingTime;
    }

    public void setCookTime(int value) {
        cookingTime = value;
    }

    public void setTotalCookTime(int value) {
        cookingTotalTime = value;
    }

    public int getTotalCookTime() {
        return cookingTotalTime;
    }

    public int getBurnTimeSinceLit() {
        return burnTimeSinceLit;
    }

    public int getBurnTimeCd() {
        return burnTimeCountdown;
    }

    public VariableCampfireBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMPFIRE, pos, state);
    }

    public static void litServerTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE) {

        int currentFireLevel = getCurrentFireLevel(state);

        if (currentFireLevel > 0) {
            // allow campfire to turn to fire block if under these blocks
            if (world.getBlockState(pos.down()).isOf(Blocks.NETHERRACK) || world.getBlockState(pos.down()).isOf(Blocks.OBSIDIAN))
            {
                world.setBlockState(pos, Blocks.FIRE.getDefaultState());
            }

            /**
            //TODO : Fire spread for campfire. NOT WORKING ATM
             if ( iCurrentFireLevel > 1 && world.random.nextFloat() <= CHANCE_OF_FIRE_SPREAD) {
                 Block fireBlock = state.getBlock();
                 fireBlock.checkForFireSpreadFromLocation(world, pos, world.random, 0);
             }
             **/

            // New try - lighting adjacent campfires only (no fire spread atm)
            if (currentFireLevel > 1 && world.random.nextFloat() <= MODIFIED_CHANCE_OF_FIRE_SPREAD) {
                for (Direction direction : Direction.values()) {

                    BlockPos adjacentPos = pos.offset(direction);
                    BlockState adjacentState = world.getBlockState(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();
                    if (adjacentBlock instanceof CampfireBlock) {
                        VariableCampfireBE adjacentCampfireBE = (VariableCampfireBE) world.getBlockEntity(adjacentPos);
                        if (adjacentCampfireBE != null && isAdjacentCampfireLightableFromSpread(adjacentState)) {
                            adjacentCampfireBE.changeFireLevel(world, 1);
                            adjacentCampfireBE.onFirstLit();
                            IgnitableBlock.playLitFX(world, pos);
                        }
                    }
                }
            }

            campfireBE.burnTimeSinceLit++;

            if (campfireBE.burnTimeCountdown > 0 ) {
                campfireBE.burnTimeCountdown--;

                if (currentFireLevel == 3 ) {
                    // blaze burns extra fast
                    campfireBE.burnTimeCountdown--;
                }
            }

            currentFireLevel = campfireBE.validateFireLevel(world, state, pos);

            if (currentFireLevel > 0) {
                boolean bl = false;

                ItemStack itemStack = campfireBE.itemsBeingCooked.getFirst();
                if (!itemStack.isEmpty()) {
                    bl = true;
                    campfireBE.setCookTime(campfireBE.getCookTime() + 1);
                    if (campfireBE.getCookTime() >= campfireBE.getTotalCookTime()) {
                        SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(itemStack);
                        ItemStack itemStack2 = campfireBE.matchGetter
                                .getFirstMatch(singleStackRecipeInput, world)
                                .map(recipe -> recipe.value().craft(singleStackRecipeInput, world.getRegistryManager()))
                                .orElse(itemStack);                        if (itemStack2.isItemEnabled(world.getEnabledFeatures()))
                        {
                            campfireBE.itemsBeingCooked.set(0, itemStack2);
                            world.updateListeners(pos, state, state, 3);
                            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                        }
                    }
                }

                if (bl) {
                    markDirty(world, pos, state);
                }

                if ( isGoOutFromRainChance(world, pos) ) {
                    campfireBE.extinguishFire(world, state, pos, false);
                }

            }
        }
    }

    private static boolean isAdjacentCampfireLightableFromSpread(BlockState state) {
        return state.get(FIRE_LEVEL) == 0 && state.get(FUEL_STATE) == CampfireState.NORMAL;
    }

    public static void unlitServerTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE) {
        boolean bl = false;

        // Decrement smoulderCounter if it's greater than zero
        if (campfireBE.smoulderCounter > 0) {
            campfireBE.smoulderCounter--;

            if (campfireBE.smoulderCounter == 0 || isGoOutFromRainChance(world, pos)) {
                campfireBE.stopSmouldering(world, pos);
            }
        }

        // Handle cooking items
        for (int i = 0; i < campfireBE.getItemsBeingCooked().size(); ++i) {
            if (campfireBE.getCookTime() <= 0) continue;
            bl = true;
            campfireBE.setCookTime(MathHelper.clamp(campfireBE.getCookTime() - 2, 0, campfireBE.getTotalCookTime()));
        }

        if (bl) {
            VariableCampfireBE.markDirty(world, pos, state);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE) {
        Random random = world.random;

        if (random.nextFloat() < 0.11f) {
            int particleCount = random.nextInt(2) + 2;
            for (int i = 0; i < particleCount; ++i) {
                CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        if (!campfireBE.itemsBeingCooked.get(0).isEmpty() && random.nextFloat() < 0.2f) {
            double d = pos.getX() + 0.5;
            double e = pos.getY() + 0.9;
            double g = pos.getZ() + 0.5;
            world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
        }
    }

    private static boolean isGoOutFromRainChance(World world, BlockPos pos) {
        return world.random.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && isRainingOnCampfire(world, pos);
    }

    public int validateFireLevel(World world, BlockState state, BlockPos pos) {
        int currentFireLevel = getCurrentFireLevel(state);

        if (currentFireLevel > 0) {
            //int iFuelState = FCBetterThanWolves.fcBlockCampfireUnlit.GetFuelState( worldObj, xCoord, yCoord, zCoord );

            if (burnTimeCountdown <= 0 ) {
                extinguishFire(world,state, pos,true);

                return 0;
            }
            else {
                int desiredFireLevel = 2;

                if (burnTimeSinceLit < WARMUP_TIME || burnTimeCountdown < REVERT_TO_SMALL_TIME) {
                    desiredFireLevel = 1;
                }
                else if (burnTimeCountdown > BLAZE_TIME) {
                    desiredFireLevel = 3;
                }

                if (desiredFireLevel != currentFireLevel) {
                    changeFireLevel(world, desiredFireLevel);

                    if (desiredFireLevel == 1 && currentFireLevel == 2) {
                        IgnitableBlock.playExtinguishSound(world, pos, false);
                    }

                    return desiredFireLevel;
                }
            }
        // currentFireLevel == 0
        }
        else {
            if (burnTimeCountdown > 0 && state.get(FUEL_STATE) == CampfireState.SMOULDERING) {
                relightSmouldering(world, pos);
                setFuelState(world, pos, CampfireState.NORMAL);
                return 1;
            }
        }

        return currentFireLevel;
    }

    // Method to set the fuel state
    public void setFuelState(World world, BlockPos pos, CampfireState fuelState) {
        world.setBlockState(pos, world.getBlockState(pos).with(FUEL_STATE, fuelState).with(Properties.WATERLOGGED, false));
    }

    private void relightSmouldering(World world, BlockPos pos) {
        burnTimeSinceLit = 0;
        BlockState state = world.getBlockState(pos);
        CampfireBlock block = (CampfireBlock) state.getBlock();
        ((CampfireBlockAdded)block).btwr$relightFire(world, pos);
    }

    private void extinguishFire(World world, BlockState state, BlockPos pos, boolean smoulder) {

        if (smoulder) {
            smoulderCounter = SMOULDER_TIME;
        } else {
            smoulderCounter = 0;
        }

        cookingTime = 0; // reset cook counter in case fire is relit later
        cookBurningCounter = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();
        ((CampfireBlockAdded)block).btwr$extinguishFire(world, state, pos, smoulder);
    }

    private void stopSmouldering(World world, BlockPos pos) {
        smoulderCounter = 0;
        CampfireBlock block = (CampfireBlock) world.getBlockState(pos).getBlock();
        ((CampfireBlockAdded)block).btwr$stopSmouldering(world, pos);
    }

    public static boolean isRainingOnCampfire(World world, BlockPos pos) {
        return world.isRaining() && world.hasRain(pos);
    }

    private static int getCurrentFireLevel(BlockState state) {
        return state.get(FIRE_LEVEL);
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.itemsBeingCooked.clear();
        Inventories.readNbt(nbt, this.itemsBeingCooked, registryLookup);
        if (nbt.contains("CookingTime")) { cookingTime = nbt.getInt("CookingTime"); }
        if (nbt.contains("CookingTotalTime")) { cookingTotalTime = nbt.getInt("CookingTotalTime"); }

        if (nbt.contains("BurnCounter")) { burnTimeCountdown = nbt.getInt("BurnCounter"); }
        if (nbt.contains("BurnTime")) { burnTimeSinceLit = nbt.getInt("BurnTime"); }
        if (nbt.contains("SmoulderCounter")) { smoulderCounter = nbt.getInt("SmoulderCounter"); }
        if (nbt.contains("CookBurning")) { cookBurningCounter = nbt.getInt("CookBurning"); }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.itemsBeingCooked, true, registryLookup);
        nbt.putInt("CookingTime", cookingTime);
        nbt.putInt("CookingTotalTime", cookingTotalTime);

        nbt.putInt("BurnCounter", burnTimeCountdown);
        nbt.putInt("BurnTime", burnTimeSinceLit);
        nbt.putInt("SmoulderCounter", smoulderCounter);
        nbt.putInt("CookBurning", cookBurningCounter);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.itemsBeingCooked, true, registryLookup);
        return nbtCompound;
    }

    public Optional<RecipeEntry<CampfireCookingRecipe>> getRecipeFor(ItemStack stack) {
        return this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)
                ? Optional.empty()
                : this.matchGetter.getFirstMatch(new SingleStackRecipeInput(stack), this.world);
    }

    public void addItem(@Nullable Entity user, ItemStack stack, int cookTime) {
        if (this.world == null || this.world.isClient) {
            return;
        }

        for (ItemStack itemStack : this.itemsBeingCooked) {
            if (!itemStack.isEmpty()) continue;
            setTotalCookTime(cookTime);
            this.setCookTime(0);
            this.itemsBeingCooked.set(0, stack.split(1));
            assert this.world != null;
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners();
            return;
        }
    }

    public void retrieveItem(World world, VariableCampfireBE campfireBE, PlayerEntity player) {
        ItemStack cookStack = campfireBE.getItemsBeingCooked().getFirst();

        if (!cookStack.isEmpty() && !world.isClient()) {
            boolean addedToInventory = player.giveItemStack(cookStack);
            if (!addedToInventory) {
                player.dropItem(cookStack, false);
            }
            itemsBeingCooked.set(0, ItemStack.EMPTY);
            campfireBE.markDirty();
            Objects.requireNonNull(campfireBE.getWorld()).updateListeners(campfireBE.getPos(), campfireBE.getCachedState(), campfireBE.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void changeFireLevel(World world, int iFireLevel) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CampfireBlock block) {
            ((CampfireBlockAdded)block).btwr$changeFireLevel(world, pos, iFireLevel);
        }
    }

    public void addBurnTime(BlockState state, int iBurnTime) {
        burnTimeCountdown += iBurnTime * CAMPFIRE_BURN_TIME_MULTIPLIER * BASE_BURN_TIME_MULTIPLIER;
        burnTimeCountdown = Math.min(burnTimeCountdown, MAX_BURN_TIME);
        validateFireLevel(world, state, pos);
    }

    public void addBurnTime(BlockState state, ItemStack stack, int iBurnTime) {
        burnTimeCountdown += iBurnTime * stack.getCount() * CAMPFIRE_BURN_TIME_MULTIPLIER * BASE_BURN_TIME_MULTIPLIER;
        burnTimeCountdown = Math.min(burnTimeCountdown, MAX_BURN_TIME);
        validateFireLevel(world, state, pos);
    }

    public void onFirstLit() {
        burnTimeCountdown = INITIAL_BURN_TIME;
        burnTimeSinceLit = 0;
    }

}