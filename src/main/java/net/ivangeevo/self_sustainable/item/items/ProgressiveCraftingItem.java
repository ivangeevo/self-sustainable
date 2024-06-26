package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class ProgressiveCraftingItem extends Item
{
    static public final int PROGRESS_TIME_INTERVAL = 4;
    static public final int DEFAULT_MAX_DAMAGE = (120 * 20 / PROGRESS_TIME_INTERVAL);

    public ProgressiveCraftingItem(Settings settings)
    {
        super(settings);
        settings.maxDamage(getProgressiveCraftingMaxDamage());
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand); // Start using the item
        return TypedActionResult.consume(user.getMainHandStack());
    }

    /**
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        // Maybe try active hand stack instead of the main ?
        ItemStack stack = user.getActiveItem();
        stack.setDamage(getMaxUseTime(stack));
        user.setStackInHand(hand, stack);
        return TypedActionResult.success(stack,false);
    }
     **/


    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks)
    {
        int iUseCount = user.getItemUseTimeLeft();

        if ( getMaxUseTime( stack ) - iUseCount > getItemUseWarmupDuration() )
        {
            if ( iUseCount % 4 == 0 )
            {
                playCraftingFX(stack, world, user);
            }

            if ( !world.isClient && iUseCount % PROGRESS_TIME_INTERVAL == 0 )
            {
                int iDamage = stack.getDamage();

                iDamage -= 1;

                if ( iDamage > 0 )
                {
                    stack.setDamage( iDamage );
                }
                else
                {
                    // set item usage to immediately complete

                    user.setItemUseTime(1);
                }
            }
        }
    }


    @Override
    public UseAction getUseAction(ItemStack stack) { return UseAction.NONE; }

    @Override
    public CustomUseAction getCustomUseAction()
    {
        return CustomUseAction.PROGRESSIVE_CRAFT;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) { return 72000; }

    @Override
    public void updateUsingItem(ItemStack stack, World world, PlayerEntity player)
    {

    }

    //------------- Class Specific Methods ------------//

    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player)
    {
    }

    protected int getProgressiveCraftingMaxDamage()
    {
        return DEFAULT_MAX_DAMAGE;
    }
}
