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


}
