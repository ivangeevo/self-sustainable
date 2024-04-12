package net.ivangeevo.self_sustainable.item.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        settings.maxCount(1);
        settings.maxDamage(getProgressiveCraftingMaxDamage());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getMainHandStack();
        user.setItemInUse(stack, getMaxUseTime(stack));
        return TypedActionResult.success(stack);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {return UseAction.DRINK;}

    @Override
    public int getMaxUseTime(ItemStack stack) {return 72000;}

    protected int getProgressiveCraftingMaxDamage()
    {
        return DEFAULT_MAX_DAMAGE;
    }

    @Override
    public void updateUsingItem(ItemStack stack, World world, PlayerEntity player)
    {
        int iUseCount = player.getItemUseTime();

        if ( getMaxUseTime( stack ) - iUseCount > getItemUseWarmupDuration() )
        {
            if ( iUseCount % 4 == 0 )
            {
                playCraftingFX(stack, world, player);
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

                    player.getMainHandStack().setCount(1);
                }
            }
        }
    }

    protected void playCraftingFX(ItemStack stack, World world, PlayerEntity player)
    {
    }
}
