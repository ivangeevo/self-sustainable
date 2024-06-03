// FCMOD

package net.ivangeevo.self_sustainable.item.items;


import net.ivangeevo.self_sustainable.block.interfaces.BlockAdded;
import net.ivangeevo.self_sustainable.item.util.DirectlyIgnitingItem;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class FireStarterItem extends Item implements DirectlyIgnitingItem
{
    private final float exhaustionPerUse;

    public FireStarterItem(Item.Settings settings, float fExhaustionPerUse )
    {
        super( settings );
        exhaustionPerUse = fExhaustionPerUse;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context)
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
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;

    }



    @Override
    public boolean getCanItemStartFireOnUse(ItemStack stack)
    {
        return true;
    }

    //------------- Class Specific Methods ------------//

    protected abstract boolean checkChanceOfStart(ItemStack stack, Random random);

    protected void performUseEffects(PlayerEntity player)
    {
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
