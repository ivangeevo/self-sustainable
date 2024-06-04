/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.self_sustainable.block;

import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
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
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Clearable;
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

import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FIRE_LEVEL;
import static net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock.FUEL_STATE;


public class VariableCampfireBE
        extends BlockEntity
        implements Clearable
{
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private int cookingTime = 0;
    private int cookingTotalTime = 0;
    public final RecipeManager.MatchGetter<Inventory, CampfireCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

    // BTW Added variables

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

    @Unique private ItemStack spitStack = null;

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

    public VariableCampfireBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAMPFIRE, pos, state);
    }

    public static void litServerTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE)
    {






        int iCurrentFireLevel = getCurrentFireLevel(state);

        if ( iCurrentFireLevel > 0 )
        {

            //TODO : Fire spread for campfire. NOT WORKING ATM
             if ( iCurrentFireLevel > 1 && world.random.nextFloat() <= CHANCE_OF_FIRE_SPREAD)
             {
                 Block fireBlock = state.getBlock();
                 if (fireBlock instanceof FireBlock fire) {
                     fire.checkForFireSpreadFromLocation(world, pos, world.random, 0);
                 }
             }



            campfireBE.burnTimeSinceLit++;


            if (campfireBE.burnTimeCountdown > 0 )
            {
                campfireBE.burnTimeCountdown--;

                if ( iCurrentFireLevel == 3 )
                {
                    // blaze burns extra fast

                    campfireBE.burnTimeCountdown--;
                }
            }

            iCurrentFireLevel = campfireBE.validateFireLevel(world, state, pos);

            if ( iCurrentFireLevel > 0 )
            {
                // TODO: Fix cookTime not incrementing correctly.
                boolean bl = false;

                ItemStack itemStack = campfireBE.itemsBeingCooked.get(0);
                if (!itemStack.isEmpty())
                {
                    bl = true;
                    campfireBE.setCookTime(campfireBE.getCookTime() + 1);
                    if (campfireBE.getCookTime() >= campfireBE.getTotalCookTime()) {
                        Inventory inventory = new SimpleInventory(itemStack);
                        ItemStack itemStack2 = campfireBE.matchGetter.getFirstMatch(inventory, world).map((recipe) -> recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);
                        if (itemStack2.isItemEnabled(world.getEnabledFeatures()))
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

                if (world.random.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && campfireBE.isRainingOnCampfire(world, pos) )
                {
                    campfireBE.extinguishFire(world, state, pos, false);
                }
            }
        }
        else if (campfireBE.smoulderCounter > 0 )
        {
            campfireBE.smoulderCounter--;

            if (campfireBE.smoulderCounter == 0 || world.random.nextFloat() <= CHANCE_OF_GOING_OUT_FROM_RAIN && campfireBE.isRainingOnCampfire(world, pos) )
            {
                campfireBE.stopSmouldering(world, pos);
            }
        }

    }

    public static void unlitServerTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE) {
        boolean bl = false;
        for (int i = 0; i < campfireBE.getItemsBeingCooked().size(); ++i) {
            if (campfireBE.getCookTime() <= 0) continue;
            bl = true;
            campfireBE.setCookTime( MathHelper.clamp(campfireBE.getCookTime() - 2, 0, campfireBE.getTotalCookTime()) );
        }
        if (bl) {
            VariableCampfireBE.markDirty(world, pos, state);
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, VariableCampfireBE campfireBE) {
        int i;
        Random random = world.random;
        if (random.nextFloat() < 0.11f) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        i = state.get(CampfireBlock.FACING).getHorizontal();
        for (int j = 0; j < campfireBE.itemsBeingCooked.size(); ++j) {
            if (campfireBE.itemsBeingCooked.get(j).isEmpty() || !(random.nextFloat() < 0.2f)) continue;
            Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
            float f = 0.3125f;
            double d = (double)pos.getX() + 0.5 - (double)((float)direction.getOffsetX() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetX() * 0.3125f);
            double e = (double)pos.getY() + 0.5;
            double g = (double)pos.getZ() + 0.5 - (double)((float)direction.getOffsetZ() * 0.3125f) + (double)((float)direction.rotateYClockwise().getOffsetZ() * 0.3125f);
            for (int k = 0; k < 4; ++k) {
                world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
            }
        }
    }

    public int validateFireLevel(World world, BlockState state, BlockPos pos)
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
                    changeFireLevel(iDesiredFireLevel);

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
                relightSmouldering(world, pos);
                return 1;
            }
        }

        return iCurrentFireLevel;
    }


    private void relightSmouldering(World world, BlockPos pos)
    {
        burnTimeSinceLit = 0;
        BlockState state = world.getBlockState(pos);
        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).relightFire(world, pos);
    }

    private void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            smoulderCounter = SMOULDER_TIME;
        }
        else
        {
            smoulderCounter = 0;
        }

        cookingTime = 0; // reset cook counter in case fire is relit later
        cookBurningCounter = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).extinguishFire(world, state, pos, bSmoulder);
    }

    private void stopSmouldering(World world, BlockPos pos)
    {
        smoulderCounter = 0;

        CampfireBlock block = (CampfireBlock) world.getBlockState(pos).getBlock();

        ((CampfireBlockAdded)block).stopSmouldering(world, pos);
    }

    public boolean isRainingOnCampfire(World world, BlockPos pos)
    {
        return world.isRaining() && world.hasRain(pos);
    }

    private static int getCurrentFireLevel(BlockState state)
    {
        return state.get(FIRE_LEVEL);
    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    public static boolean addItem(Entity user, ItemStack stack, int cookTime, VariableCampfireBE be)
    {
        be.cookingTotalTime = cookTime;
        be.cookingTime = 0;
        be.getItemsBeingCooked().set(0, stack.split(1));
        be.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, be.getPos(), GameEvent.Emitter.of(user, be.getCachedState()));
        be.getWorld().updateListeners(be.getPos(), be.getCachedState(), be.getCachedState() , Block.NOTIFY_ALL);

        return true;

    }


    public void retrieveItem(World world, VariableCampfireBE campfireBE, PlayerEntity player)
    {
        DefaultedList<ItemStack> itemsBeingCooked = campfireBE.getItemsBeingCooked();

        if (!itemsBeingCooked.isEmpty() && !world.isClient())
        {
            ItemStack itemStack = itemsBeingCooked.get(0);
            if (!itemStack.isEmpty()) {
                player.giveItemStack(itemStack);
                itemsBeingCooked.set(0, ItemStack.EMPTY);
                campfireBE.markDirty();
                Objects.requireNonNull(campfireBE.getWorld()).updateListeners(campfireBE.getPos(), campfireBE.getCachedState(), campfireBE.getCachedState(), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.itemsBeingCooked.clear();
        Inventories.readNbt(nbt, this.itemsBeingCooked);
        if (nbt.contains("CookingTime")) { cookingTime = nbt.getInt("CookingTime"); }
        if (nbt.contains("CookingTotalTime")) { cookingTotalTime = nbt.getInt("CookingTotalTime"); }

        if (nbt.contains("BurnCounter")) { burnTimeCountdown = nbt.getInt("BurnCounter"); }
        if (nbt.contains("BurnTime")) { burnTimeSinceLit = nbt.getInt("BurnTime"); }
        if (nbt.contains("SmoulderCounter")) { smoulderCounter = nbt.getInt("SmoulderCounter"); }
        if (nbt.contains("CookBurning")) { cookBurningCounter = nbt.getInt("CookBurning"); }

    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.itemsBeingCooked, true);
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
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.itemsBeingCooked, true);
        return nbtCompound;
    }

    public Optional<CampfireCookingRecipe> getRecipeFor(ItemStack stack) {
        if (this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        return this.matchGetter.getFirstMatch(new SimpleInventory(stack), this.world);
    }

    public boolean addItem(@Nullable Entity user, ItemStack stack, int cookTime) {
        for (ItemStack itemStack : this.itemsBeingCooked) {
            if (!itemStack.isEmpty()) continue;
            setTotalCookTime(cookTime);
            this.setCookTime(0);
            this.itemsBeingCooked.set(0, stack.split(1));
            assert this.world != null;
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners();
            return true;
        }
        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void spawnItemsBeingCooked() {
        if (this.world != null) {
            this.updateListeners();
        }
    }


    public void changeFireLevel(int iFireLevel) {

        assert world != null;
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof CampfireBlock block)
        {
            ((CampfireBlockAdded)block).changeFireLevel(world, pos, iFireLevel);

        }

    }

    public void setSpitStack(ItemStack stack)
    {
        if ( stack != null )
        {
            spitStack = stack.copy();

            spitStack.setCount(1);
        }
        else
        {
            spitStack = null;
        }

        markDirty();
    }

    public ItemStack getSpitStack() {
        return spitStack;
    }

    public void addBurnTime(BlockState state, int iBurnTime) {
        burnTimeCountdown += iBurnTime * CAMPFIRE_BURN_TIME_MULTIPLIER * BASE_BURN_TIME_MULTIPLIER;

        burnTimeCountdown = Math.min(burnTimeCountdown, MAX_BURN_TIME);

        validateFireLevel(world, state, pos);
    }


    public void onFirstLit() {
        burnTimeCountdown = INITIAL_BURN_TIME;
        burnTimeSinceLit = 0;
    }
}

