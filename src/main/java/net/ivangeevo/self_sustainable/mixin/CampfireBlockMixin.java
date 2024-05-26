package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.CampfireBlockManager;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.ivangeevo.self_sustainable.tag.BTWRConventionalTags;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.ivangeevo.self_sustainable.block.interfaces.VariableCampfireBlock.HAS_SPIT;


@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements Ignitable, CampfireBlockAdded
{


    // BTW added variables (modernized probably)


    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }


    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedConstructor(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci)
    {
        this.setDefaultState(this.getStateManager().getDefaultState().with(LIT, false).with(HAS_SPIT, false));
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir)
    {
        cir.setReturnValue(cir.getReturnValue().with(CampfireBlock.LIT, false));
    }

    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void addedCustomProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
    {
        CampfireBlockManager.appendCustomProperties(builder);
        ci.cancel();
    }


    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCustomOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        cir.setReturnValue(CampfireBlockManager.setCustomShapes(state));

    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (state.getBlock() == Blocks.CAMPFIRE)
        {
            cir.setReturnValue(CampfireBlockManager.onUse(state, world, pos, player, hand, hit));
        }


    }











    @Override
    public CampfireState getFuelState(BlockState state) {
        return state.get(FUEL_STATE);
    }

    @Override
    public int getFireLevel(BlockState state) {
        return state.get(FIRE_LEVEL);
    }

    @Override
    public BlockState setFireLevel (BlockState state, int newLevel)
    {
        return state.with(FIRE_LEVEL, newLevel);
    }

    public boolean isValidCookItem(World world, BlockPos pos, PlayerEntity player) {
        ItemStack itemStack;
        CampfireBlockEntity campfireBlockEntity;
        Optional<CampfireCookingRecipe> optional;
        Hand hand = player.getActiveHand();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof CampfireBlockEntity
                && (optional = (campfireBlockEntity = (CampfireBlockEntity) blockEntity).getRecipeFor(itemStack = player.getStackInHand(hand))).isPresent();
    }



    @Override
    public void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            setFuelState(state, CampfireState.SMOULDERING.ordinal());
        }
        else
        {
            setFuelState(state, CampfireState.BURNED_OUT.ordinal());
        }

        changeFireLevel(state, 0);

        if ( !world.isClient() )
        {
            playExtinguishSound(world, pos, true);
        }
    }

    @Unique
    public BlockState setFuelState(BlockState currentState, int fireState)
    {
        return currentState.with(FUEL_STATE, CampfireState.convertToEnumState(fireState));
    }

    public void relightFire(BlockState state)
    {
        changeFireLevel(state, 1);
    }
















    /**
    @Unique
    private void setOrRemoveItems(World world, PlayerEntity player, Hand hand, BlockState state, BlockPos pos) {
        ItemStack itemStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CampfireBlockEntity campfireBlockEntity) {
            Optional<CampfireCookingRecipe> optional = campfireBlockEntity.getRecipeFor(itemStack);

            // Condition 1: Handling the addition or removal of the spit
            if (state.get(HAS_SPIT))
            {
                world.setBlockState(pos, state.with(HAS_SPIT, false));
                player.giveItemStack(new ItemStack(Items.STICK));

            }
            else
            {
                if (itemStack.isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS))
                {
                    world.setBlockState(pos, state.with(HAS_SPIT, true));
                    itemStack.decrement(1);
                }
            }


            /**
            if (state.get(HAS_SPIT) && optional.isPresent()) {
                if (((CampfireBlockEntityAdded) campfireBlockEntity).getItemBeingCooked() == null) {
                    CampfireCookingRecipe recipe = optional.get();
                    campfireBlockEntity.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, recipe.getCookTime());
                } else {
                    ItemStack retrievedItem = ((CampfireBlockEntityAdded) campfireBlockEntity).retrieveItem(player);
                    if (retrievedItem != null && !player.isCreative()) {
                        if (!player.giveItemStack(retrievedItem)) {
                            player.dropItem(retrievedItem, false);
                        }
                    }
                }

            }

        }
    }
     **/

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        Optional<NetherPortal> optional;
        if (oldState.isOf(state.getBlock()))
        {
            return;
        }
        /**
        if (isOverworldOrNether(world)
                && (optional = NetherPortal.getNewPortal(world, pos, Direction.Axis.X)).isPresent() && state.get(LIT))
        {
            optional.get().createPortal();
            return;
        }
         **/
        if (!state.canPlaceAt(world, pos))
        {
            world.removeBlock(pos, false);
        }

    }

    @Unique
    private static boolean isOverworldOrNether(World world)
    {
        return world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.NETHER;
    }



}
