package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.ivangeevo.self_sustainable.item.util.DirectlyIgnitingItem;
import net.ivangeevo.self_sustainable.util.WorldUtils;
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
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//TODO: FIX
@Mixin(FlintAndSteelItem.class)
public abstract class FlintAndSteelItemMixin implements DirectlyIgnitingItem
{
    @Unique private final float exhaustionPerUse = 0.01F;

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void customUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (world.canPlayerModifyAt(player, pos))
        {
            performUseEffects(player);

            if (!world.isClient)
            {
                //notifyNearbyAnimalsOfAttempt(player);

                if (checkChanceOfStart(context.getStack(), world.random))
                {
                    attemptToLightBlock(context.getStack(), world, pos, context.getSide());
                }
            }

            assert player != null;
            player.addExhaustion(exhaustionPerUse * world.getDifficulty().getHungerIntensiveActionCostMultiplier());
            context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
            cir.setReturnValue( ActionResult.SUCCESS ) ;
        }

        cir.setReturnValue( ActionResult.FAIL );

    }

    @Override
    public void performUseEffects(PlayerEntity player)
    {
        player.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, player.getRandom().nextFloat() * 0.4F + 0.8F );
    }


    @Override
    public boolean checkChanceOfStart(ItemStack stack, Random rand)
    {
        return rand.nextInt( 4 ) == 0;
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
