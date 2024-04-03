// FCMOD

package net.ivangeevo.self_sustainable.item.items;


import com.google.common.base.Predicates;
import net.ivangeevo.self_sustainable.block.interfaces.BlockAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;

public abstract class FireStarterItem extends Item
{
    private final float exhaustionPerUse;

    public FireStarterItem(Item.Settings settings, float fExhaustionPerUse )
    {
        super( settings );
        //settings.maxDamageIfAbsent( iMaxUses );
        exhaustionPerUse = fExhaustionPerUse;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack useStack = user.getActiveItem();
        MinecraftClient client = MinecraftClient.getInstance();
        BlockHitResult hitResult = (BlockHitResult) client.crosshairTarget;

        if (hitResult != null) {
            BlockPos hitPos = hitResult.getBlockPos();
            Direction hitSide = hitResult.getSide();
        if ( user.canModifyAt(world,hitPos) )
        {
            performUseEffects(user);

            if (!world.isClient) {
                //notifyNearbyAnimalsOfAttempt(user);

                if (checkChanceOfStart(useStack, world.getRandom())) {
                    attemptToLightBlock(useStack, world, hitPos, hitSide);
                }
            }
        }

            user.addExhaustion(exhaustionPerUse * world.getDifficulty().getHungerIntensiveActionCostMultiplier());

            useStack.damage(1, user, (e) -> {
                e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });

            return TypedActionResult.success(useStack);
        }

        return TypedActionResult.fail(useStack);
    }

    @Override
    public boolean getCanItemStartFireOnUse(int iItemDamage)
    {
        return true;
    }

    //------------- Class Specific Methods ------------//

    protected abstract boolean checkChanceOfStart(ItemStack stack, Random random);

    protected void performUseEffects(PlayerEntity player)
    {
    }

    protected boolean attemptToLightBlock(ItemStack stack, World world, BlockPos pos, Direction facing)
    {
        Block targetBlock = world.getBlockState(pos).getBlock();

        if ( targetBlock != null && ((BlockAdded)targetBlock).getCanBeSetOnFireDirectlyByItem(world, pos) )
        {
            return ((BlockAdded)targetBlock).setOnFireDirectly(world, pos);
        }

        return false;
    }


/**
    public void notifyNearbyAnimalsOfAttempt(PlayerEntity player)
    {
        List<AnimalEntity> animalList = player.getWorld().getEntitiesByClass( AnimalEntity.class, player.getBoundingBox().expand( 6, 6, 6 ), Predicates.instanceOf(AnimalEntity.class));

        for (AnimalEntity tempAnimal : animalList) {
            if (!tempAnimal.isDead()) {
                tempAnimal.onNearbyFireStartAttempt(player);
            }
        }
    }
 **/

    //----------- Client Side Functionality -----------//
}
