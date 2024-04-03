package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemAdded
{
    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int fuelTicks) {
        return !getCanItemBeSetOnFireOnUse(fuelTicks) && !getCanItemStartFireOnUse(fuelTicks) &&
                getOvenBurnTime(fuelTicks) > 0;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int fuelTicks)
    {
        return !getCanItemBeSetOnFireOnUse(fuelTicks) && !getCanItemStartFireOnUse(fuelTicks) &&
                getCampfireBurnTime(fuelTicks) > 0;
    }

    @Override
    public int getCampfireBurnTime(int iItemDamage)
    {
        return getCampfireBurnTime(iItemDamage);
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(int fuelTicks) {

        return false;
    }

    @Override
    public boolean getCanItemStartFireOnUse(int fuelTicks) {
        return false;
    }

    @Override
    public int getOvenBurnTime(int fuelTicks) {
        return defaultFurnaceBurnTime;
    }

    @Override
    public int getItemUseWarmupDuration() {
        return 7;
    }
}
