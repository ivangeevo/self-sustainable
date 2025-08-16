package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.entity.LivingEntity;
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

    public ProgressiveCraftingItem(Settings settings) {
        super(settings);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        // Stupid large so it's never actually hit in practice
        return 72000;
    }


    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand); // Start using the item
        return TypedActionResult.consume(user.getMainHandStack());
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int useCount = user.getItemUseTimeLeft();

        boolean canUse = getMaxUseTime(stack, user) - useCount > getItemUseWarmupDuration();

        if (!canUse) return;

        if (useCount % 4 == 0) {
            playCraftingFX(stack, world, user);
        }

        boolean canContinueUse = !world.isClient && useCount % PROGRESS_TIME_INTERVAL == 0;

        if (!canContinueUse) return;

        int dmg = stack.getDamage();

        dmg -= 1;

        if (dmg > 0) {
            stack.setDamage(dmg);
        } else {
            // set item usage to immediately complete
            user.setItemUseTime(1);
        }
    }

    //------------- Class Specific Methods ------------//

    /** Effects that happen during the progressive crafting process **/
    protected void playCraftingFX(ItemStack stack, World world, LivingEntity player) {
    }

}
