package net.ivangeevo.self_sustainable.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin
{

    //@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void customUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState))
        {
            BlockPos blockPos2 = blockPos.offset(context.getSide());

            if (AbstractFireBlock.canPlaceAt(world, blockPos2, context.getHorizontalPlayerFacing())) {
                cir.setReturnValue( ActionResult.FAIL );
            }
            else
            {
                cir.setReturnValue( ActionResult.FAIL );
            }

        }
        else
        {
            world.playSound(playerEntity, blockPos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
                    1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);

            world.setBlockState(blockPos, blockState.with(Properties.LIT, true), 11);

            world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, blockPos);
            if (!world.isClient() )
            {
                assert playerEntity != null;
                playerEntity.addExhaustion(30.0f);
                context.getStack().damage(1, playerEntity, (p) -> p.sendToolBreakStatus(context.getHand()));

            }




            cir.setReturnValue( ActionResult.success(world.isClient()) );
        }
    }
}
