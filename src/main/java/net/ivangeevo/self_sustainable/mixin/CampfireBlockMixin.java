package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin extends BlockWithEntity
{
    @Unique private static final BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;


    protected CampfireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void construct(boolean emitsParticles, int fireDamage, Settings settings, CallbackInfo ci) {
        this.setDefaultState(getDefaultState().with(CampfireBlock.LIT, false));
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void getPlacementState(ItemPlacementContext context, CallbackInfoReturnable<BlockState> cir) {
        cir.setReturnValue(cir.getReturnValue().with(CampfireBlock.LIT, false));
    }

    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).isIn(ModTags.Items.CAMPFIRE_IGNITER_ITEMS) && CampfireBlock.canBeLit(state) && world.setBlockState(pos, state.with(CampfireBlock.LIT, true))) {
            player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);

            ItemStack heldStack = player.getMainHandStack();
            heldStack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
            this.playLitFX(world, pos);

            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

    @Unique
    private void playLitFX(World world, BlockPos pos) {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);

    }

}
