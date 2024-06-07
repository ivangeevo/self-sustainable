package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.interfaces.ItemStackAdded;
import net.ivangeevo.self_sustainable.item.util.DirectlyIgnitingItem;
import net.ivangeevo.self_sustainable.util.WorldUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
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
public abstract class FlintAndSteelItemMixin extends Item implements DirectlyIgnitingItem
{
    @Unique private final float exhaustionPerUse = 0.01F;

    public FlintAndSteelItemMixin(Settings settings) {
        super(settings);
    }

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
        player.playSound(SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS,
                0.5f + 0.5f * (float)player.getRandom().nextInt(2),
                (player.getRandom().nextFloat() * 0.25f) + 1.75f);

        if (!player.getWorld().isClient())
        {
            for (int var3 = 0; var3 < 5; ++var3)
            {
                Vec3d var4 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);

                var4 = var4.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var4 = var4.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                Vec3d var5 = new Vec3d((player.getRandom().nextFloat() - 0.5) * 0.3, (-player.getRandom().nextFloat()) * 0.6 - 0.3, 0.6);

                var5 = var5.rotateX(-player.getPitch() * (float)Math.PI / 180.0f);
                var5 = var5.rotateY(-player.getYaw() * (float)Math.PI / 180.0f);

                var5 = var5.add(player.getX(), player.getY() + player.getEyeHeight(player.getPose()), player.getZ());

                player.getWorld().addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getDefaultStack()), var5.getX(), var5.getY(), var5.getZ(), var4.getX(), var4.getY() + 0.05, var4.getZ());
            }
        }
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
