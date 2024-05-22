package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
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
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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


@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity implements Ignitable
{
    @Shadow @Final public static BooleanProperty LIT;
    @Shadow @Final public static BooleanProperty SIGNAL_FIRE;
    @Shadow @Final public static BooleanProperty WATERLOGGED;
    @Shadow @Final public static DirectionProperty FACING;
    @Shadow @Final protected static VoxelShape SHAPE;

    // Added variables
    @Unique private static final BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;

    @Unique private static final VoxelShape SHAPE_WITH_SPIT = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0));




    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void injectedConstructor(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci)
    {
        this.setDefaultState(this.getStateManager().getDefaultState().with(CampfireBlock.LIT, false).with(HAS_SPIT, false));
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir)
    {
        cir.setReturnValue(cir.getReturnValue().with(CampfireBlock.LIT, false));
    }

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void injectedCustomOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir)
    {
        if (!state.get(HAS_SPIT))
        {
            cir.setReturnValue(SHAPE);
        }
        else
        {
            cir.setReturnValue(SHAPE_WITH_SPIT);
        }
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (!world.isClient)
        {
            this.igniteCampfire(world, player, hand, state, pos);
            this.setOrRemoveSpit(world, player, state, pos);
            this.addOrRetrieveItem(world, player, pos);
            cir.setReturnValue(ActionResult.SUCCESS);
        }

    }

    @Unique
    private void igniteCampfire(World world, PlayerEntity player, Hand hand, BlockState state, BlockPos pos)
    {
        if (player.getStackInHand(hand).isIn(ModTags.Items.CAMPFIRE_IGNITER_ITEMS) && CampfireBlock.canBeLit(state)
                && world.setBlockState(pos, state.with(CampfireBlock.LIT, true)))
        {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            ItemStack heldStack = player.getMainHandStack();
            heldStack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
            this.playLitFX(world, pos);

        }
    }

    @Unique
    private void setOrRemoveSpit(World world, PlayerEntity player, BlockState state, BlockPos pos)
    {
        ItemStack heldStack = player.getMainHandStack();

        if ( !state.get(HAS_SPIT) )
        {
            if ( heldStack.isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS) )
            {
                heldStack.decrement(1);
                world.setBlockState(pos, state.with(HAS_SPIT, true));
            }
        }
        else
        {
            if ( heldStack.isEmpty() )
            {
                player.giveItemStack(new ItemStack(Items.STICK));
                world.setBlockState(pos, state.with(HAS_SPIT, false));
            }
        }

    }

    @Unique
    private void addOrRetrieveItem(World world, PlayerEntity player, BlockPos pos)
    {
        ItemStack heldStack = player.getMainHandStack();
        CampfireBlockEntity campfireBlockEntity;
        Optional<CampfireCookingRecipe> optional;
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CampfireBlockEntity)
        {
            campfireBlockEntity = (CampfireBlockEntity) blockEntity;

            // Check for cooking
            if (!heldStack.isEmpty() && (optional = campfireBlockEntity.getRecipeFor(heldStack)).isPresent())
            {
                if (!world.isClient)
                {
                    campfireBlockEntity.addItem(player, player.getAbilities().creativeMode ? heldStack.copy() :
                            heldStack, optional.get().getCookTime());
                }
            }
            else
            {
                // Check for retrieving
                ItemStack retrievedItem = ((CampfireBlockEntityAdded)campfireBlockEntity).retrieveItem(player);
                if (retrievedItem != null && !player.isCreative())
                {
                    if (!player.giveItemStack(retrievedItem))
                    {
                        player.dropItem(retrievedItem, false);
                    }
                }
            }
        }


    }


    @Inject(method = "appendProperties", at = @At("HEAD"), cancellable = true)
    private void injectedProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci)
    {
        builder.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING, HAS_SPIT);
        ci.cancel();
    }



    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        Optional<NetherPortal> optional;
        if (oldState.isOf(state.getBlock()))
        {
            return;
        }
        if (isOverworldOrNether(world)
                && (optional = NetherPortal.getNewPortal(world, pos, Direction.Axis.X)).isPresent() && state.get(LIT))
        {
            optional.get().createPortal();
            return;
        }
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
