package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CampfireBlock.class)
public abstract class CampfireBlockMixin
{
    @Unique private static final BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;


    //@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void injectedOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CampfireBlockEntity campfireBE)
        {
            // Adding spit item to campfire
            if (!world.isClient && !state.get(HAS_SPIT)
                    && player.getMainHandStack().isIn(ModTags.Items.SPIT_CAMPFIRE_ITEMS))
            {
                world.setBlockState(pos, state.with(HAS_SPIT, true));
            }


            ItemStack itemStack = player.getStackInHand(hand);
            Optional<CampfireCookingRecipe> optional = campfireBE.getRecipeFor(itemStack);

            if (optional.isPresent())
            {
                if (!world.isClient && campfireBE.addItem(player, player.getAbilities().creativeMode ? itemStack.copy() : itemStack, optional.get().getCookTime())) {
                    player.incrementStat(Stats.INTERACT_WITH_CAMPFIRE);
                    cir.setReturnValue(ActionResult.SUCCESS);
                }

                cir.setReturnValue(ActionResult.CONSUME);
            }
        }

        cir.setReturnValue(ActionResult.PASS);

    }

}
