package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.util.CampfireExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.util.MiscUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ivangeevo.self_sustainable.block.entity.BrickOvenBE.BASE_BURN_TIME_MULTIPLIER;
import static net.minecraft.block.entity.AbstractFurnaceBlockEntity.DEFAULT_COOK_TIME;


@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityAdded
{

    @Shadow protected abstract void updateListeners();

    @Shadow @Final private DefaultedList<ItemStack> itemsBeingCooked;
    @Shadow @Final private int[] cookingTotalTimes;
    @Shadow @Final private int[] cookingTimes;

    @Shadow public abstract DefaultedList<ItemStack> getItemsBeingCooked();

    @Unique
    private int litTime = 0;

    private static final int CAMPFIRE_BURN_TIME_MULTIPLIER = 8;

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

    @Unique private static int cookingTime = 0;
    @Unique private static int cookingTotalTime = 0;

    // BTW Added variables
    @Unique private ItemStack spitStack = null;

    private int burnTimeCountdown = 0;
    private int burnTimeSinceLit = 0;
    private int cookCounter = 0;
    private int smoulderCounter = 0;
    private int cookBurningCounter = 0;




    @Override
    public int getLitTime() {
        return litTime;
    }

    @Override
    public void setLitTime(int value) {
        this.litTime = value;
    }

    @Override
    public int getCookTime() {
        return cookingTime;
    }

    @Override
    public void setCookTime(int value) {
        cookingTime = value;
    }

    @Override
    public int getTotalCookTime() {
        return cookingTotalTime;
    }

    @Override
    public void setTotalCookTime(int value) {
        cookingTotalTime = value;
    }



    @Unique private static final RecipeManager.MatchGetter<Inventory, CampfireCookingRecipe> recipeMatchGetter =
            RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

    public CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Inject(method = "addItem", at = @At("HEAD"), cancellable = true)
    private void injectedAddItem(Entity user, ItemStack stack, int cookTime, CallbackInfoReturnable<Boolean> cir)
    {
        if (this.itemsBeingCooked.isEmpty())
        {
            cir.setReturnValue( false );
        }

        // Only attempt to add the item to the first slot
        ItemStack itemStack = this.itemsBeingCooked.get(0);
        if (itemStack.isEmpty()) {
            this.cookingTotalTimes[0] = cookTime;
            this.cookingTimes[0] = 0;
            this.itemsBeingCooked.set(0, stack.split(1));
            this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners();
            cir.setReturnValue( true );
        }

        cir.setReturnValue( false );

    }

    @Inject(method = "litServerTick", at = @At("HEAD"), cancellable = true)
    private static void injectedExtinguishLogic(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        // Adding extinguishing logic.
        CampfireExtinguisher.onLitServerTick(world, pos, state, campfire);

        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded entity;
        entity = (CampfireBlockEntityAdded) campfire;

        boolean bl = false;

        for (int i = 0; i < campfire.getItemsBeingCooked().size(); ++i)
        {
            ItemStack itemStack = campfire.getItemsBeingCooked().get(i);
            if (!itemStack.isEmpty()) {
                bl = true;
                int var10002 = campfire.cookingTimes[i]++;
                if (campfire.cookingTimes[i] >= campfire.cookingTotalTimes[i]) {
                    Inventory inventory = new SimpleInventory(itemStack);
                    ItemStack itemStack2 = campfire.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                            recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);

                    if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                        campfire.getItemsBeingCooked().set(i, itemStack2);
                        world.updateListeners(pos, state, state, 3);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    }

                }
            }
        }

        if (bl) {
            CampfireBlockEntity.markDirty(world, pos, state);
        }
        ci.cancel();


    }

    @Inject(method = "unlitServerTick", at = @At("HEAD"), cancellable = true)
    private static void injectedUnlitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        /**
        boolean bl = false;

        // Use this cast because it has all new variables in its scope.
        CampfireBlockEntityAdded entity;
        entity = (CampfireBlockEntityAdded) campfire;

        if (!entity.getCookStack().isEmpty())
        {
            if (entity.getCookTime() > 0) {
                bl = true;
                entity.setCookTime(MathHelper.clamp(entity.getCookTime() - 2, 0, entity.getTotalCookTime()));
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }
        ci.cancel();
     **/
    }

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private static void injectedClientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        Random random = world.random;

        // Random Smoke Particle Generation
        if (random.nextFloat() < 0.11f)
        {
            CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
        }

        // Smoke Particles for Cooking Items
        if (!campfire.getItemsBeingCooked().get(0).isEmpty() && random.nextFloat() < 0.2f)
        {
            Direction direction = state.get(CampfireBlock.FACING);
            float offset = 0.3125f;
            double d = pos.getX() + 0.5;
            double e = pos.getY() + 1.0; // Centered on Y axis at 1.0
            double g = pos.getZ() + 0.5;

            world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
        }

        ci.cancel();
    }




    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("LitTime"))
        {
            litTime = nbt.getInt("LitTime");
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putInt("LitTime", litTime);
    }

    @Nullable @Override
    public ItemStack retrieveItem(@Nullable Entity user)
    {

            ItemStack stack = this.itemsBeingCooked.get(0);
            if (!stack.isEmpty()) {
                // Optionally handle different retrieval logic here
                this.cookingTimes[0] = 0;
                this.itemsBeingCooked.set(0, ItemStack.EMPTY); // Clear the slot
                assert this.world != null;
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();

                // Return a copy of the retrieved item
                return stack.copy();
            }

        return ItemStack.EMPTY; // Return an empty stack if no item was retrieved
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

    public ItemStack getSpitStack()
    {
        return spitStack;
    }


    @Override
    public void addBurnTime(BlockState state, int iBurnTime) {
        burnTimeCountdown += iBurnTime * CAMPFIRE_BURN_TIME_MULTIPLIER * BASE_BURN_TIME_MULTIPLIER;

        burnTimeCountdown = Math.min(burnTimeCountdown, MAX_BURN_TIME);

        validateFireLevel(world, state, pos);
    }

    @Override
    public void onFirstLit() {
        burnTimeCountdown = INITIAL_BURN_TIME;
        burnTimeSinceLit = 0;
    }

    public int validateFireLevel(World world, BlockState state, BlockPos pos) {
        int iCurrentFireLevel = getCurrentFireLevel( world, state, pos);

        if (iCurrentFireLevel > 0) {
            if (burnTimeCountdown <= 0) {
                extinguishFire(state,true);
                return 0;
            } else {
                int iDesiredFireLevel = 2;

                if (burnTimeSinceLit < WARMUP_TIME || burnTimeCountdown < REVERT_TO_SMALL_TIME) {
                    iDesiredFireLevel = 1;
                } else if (burnTimeCountdown > BLAZE_TIME) {
                    iDesiredFireLevel = 3;
                }

                if (iDesiredFireLevel != iCurrentFireLevel) {
                    changeFireLevel(state, iDesiredFireLevel);

                    if (iDesiredFireLevel == 1 && iCurrentFireLevel == 2) {
                        world.playSound(null, getPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    }

                    return iDesiredFireLevel;
                }
            }
        } else {
            CampfireBlock block = (CampfireBlock) world.getBlockState(pos).getBlock();

            if (burnTimeCountdown > 0 && ((CampfireBlockAdded)block).getFuelState(state) == CampfireState.SMOULDERING) {
                relightSmouldering(state);
                return 1;
            }
        }

        return iCurrentFireLevel;
    }

    private int getCurrentFireLevel(World world, BlockState state, BlockPos pos)
    {
        CampfireBlock block = (CampfireBlock) world.getBlockState(pos).getBlock();

        return ((CampfireBlockAdded)block).getFireLevel(state);
    }


    private void extinguishFire(BlockState state, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            smoulderCounter = SMOULDER_TIME;
        }
        else
        {
            smoulderCounter = 0;
        }

        cookCounter = 0; // reset cook counter in case fire is relit later
        cookBurningCounter = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).extinguishFire(world, state, pos, bSmoulder);
    }

    @Override
    public void changeFireLevel(BlockState state, int iFireLevel)
    {
        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).changeFireLevel(state, iFireLevel);
    }

    private void relightSmouldering(BlockState state)
    {
        burnTimeSinceLit = 0;

        CampfireBlock block = (CampfireBlock) state.getBlock();

        ((CampfireBlockAdded)block).relightFire(state);
    }



}
