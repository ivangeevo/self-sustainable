package net.ivangeevo.self_sustainable.mixin;

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
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    @Unique private static final BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;


    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci)
    {
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(CampfireBlock.LIT, false)
                .with(HAS_SPIT, false)
        );
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir)
    {
        cir.setReturnValue(cir.getReturnValue().with(CampfireBlock.LIT, false));
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if (player.getStackInHand(hand).isIn(ModTags.Items.CAMPFIRE_IGNITER_ITEMS) && CampfireBlock.canBeLit(state) && world.setBlockState(pos, state.with(CampfireBlock.LIT, true)))
        {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
            ItemStack heldStack = player.getMainHandStack();
            heldStack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
            this.playLitFX(world, pos);

            cir.setReturnValue(ActionResult.SUCCESS);
        }

        if (player.getStackInHand(hand).isIn(BTWRConventionalTags.Items.SPIT_CAMPFIRE_ITEMS) && !state.get(HAS_SPIT))
        {
            ItemStack heldStack = player.getMainHandStack();
            heldStack.decrement(1);
            world.setBlockState(pos, state.with(HAS_SPIT, true));

            cir.setReturnValue(ActionResult.SUCCESS);

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
