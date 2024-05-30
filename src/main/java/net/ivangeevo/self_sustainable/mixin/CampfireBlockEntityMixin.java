package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.CampfireBEManager;
import net.ivangeevo.self_sustainable.block.entity.util.CampfireExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.util.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;



@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityAdded
{
    @Unique private int litTime = 0;
    @Unique private static final int BASE_BURN_TIME_MULTIPLIER = 2;
    @Unique private static final int CAMPFIRE_BURN_TIME_MULTIPLIER = 8;
    @Unique private static final int DEFAULT_COOK_TIME = 400;
    @Unique private static final int TIME_TO_COOK = (DEFAULT_COOK_TIME * CAMPFIRE_BURN_TIME_MULTIPLIER *
            3 / 2 ); // this line represents efficiency relative to furnace cooking
    @Unique private static final int MAX_BURN_TIME = (5 * MiscUtils.TICKS_PER_MINUTE);
    @Unique private static final int INITIAL_BURN_TIME = (50 * 4 * CAMPFIRE_BURN_TIME_MULTIPLIER *
            BASE_BURN_TIME_MULTIPLIER); // 50 is the furnace burn time of a shaft
    @Unique private static final int WARMUP_TIME = (10 * MiscUtils.TICKS_PER_SECOND);
    @Unique private static final int REVERT_TO_SMALL_TIME = (20 * MiscUtils.TICKS_PER_SECOND);
    @Unique private static final int BLAZE_TIME = (INITIAL_BURN_TIME * 3 / 2 );
    @Unique private static final int SMOULDER_TIME = (5 * MiscUtils.TICKS_PER_MINUTE); // used to be 2 minutes
    @Unique private static final int TIME_TO_BURN_FOOD = (TIME_TO_COOK / 2 );
    @Unique private static final float CHANCE_OF_FIRE_SPREAD = 0.05F;
    @Unique private static final float CHANCE_OF_GOING_OUT_FROM_RAIN = 0.01F;

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


    public CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Inject(method = "addItem", at = @At("HEAD"), cancellable = true)
    private void injectedAddItem(Entity user, ItemStack stack, int cookTime, CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue( CampfireBEManager.addItem(user, stack, cookTime, (CampfireBlockEntity) (Object)this) );
    }

    @Inject(method = "litServerTick", at = @At("HEAD"), cancellable = true)
    private static void injectedExtinguishLogic(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci)
    {
        CampfireExtinguisher.handleExtinguishing(world, pos, state, blockEntity);
        CampfireBEManager.onLitServerTick(world, pos, state, blockEntity);
        ci.cancel();


    }

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private static void injectedClientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity blockEntity, CallbackInfo ci)
    {
        CampfireBEManager.onClientTick(world, pos, state, blockEntity);
        ci.cancel();
    }

    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("LitTime")) { litTime = nbt.getInt("LitTime"); }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putInt("LitTime", litTime);
    }

    @Override
    public void retrieveItem(World world, CampfireBlockEntity campfireBE, PlayerEntity player)
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

    @Override
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
