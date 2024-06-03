package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.util.DirectlyIgnitingItem;
import net.minecraft.block.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: FIX
@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin implements DirectlyIgnitingItem
{

    //@Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void customUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        ItemStack stack = context.getStack();


        attemptToLightBlock(stack, world, pos, context.getSide());


        if (!CampfireBlock.canBeLit(blockState) && !CandleBlock.canBeLit(blockState) && !CandleCakeBlock.canBeLit(blockState))
        {
            BlockPos blockPos2 = pos.offset(context.getSide());

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
            world.playSound(playerEntity, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
                    1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);

            world.setBlockState(pos, blockState.with(Properties.LIT, true), 11);

            world.emitGameEvent(playerEntity, GameEvent.BLOCK_CHANGE, pos);
            if (!world.isClient() )
            {
                assert playerEntity != null;
                playerEntity.addExhaustion(30.0f);
                context.getStack().damage(1, playerEntity, (p) -> p.sendToolBreakStatus(context.getHand()));

            }





            cir.setReturnValue( ActionResult.success(world.isClient()) );
        }
    }

    @Override
    public boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing)
    {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if ( targetBlock != null && targetBlock.getCanBeSetOnFireDirectlyByItem(world, pos) )
        {
            return targetBlock.setOnFireDirectly(world, pos);
        }

        return false;
    }
}
