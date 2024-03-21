package net.ivangeevo.selfsustainable.mixin;

import net.ivangeevo.selfsustainable.item.interfaces.ItemAdded;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemAdded {
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
