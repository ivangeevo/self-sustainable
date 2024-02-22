package net.ivangeevo.selfsustainable.mixin;

import net.ivangeevo.selfsustainable.item.interfaces.ItemAdded;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ItemAdded {
    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage) {
        return !getCanItemBeSetOnFireOnUse(iItemDamage) && !getCanItemStartFireOnUse(iItemDamage) &&
                getFurnaceBurnTime(iItemDamage) > 0;    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(int iItemDamage) {
        return false;
    }

    @Override
    public boolean getCanItemStartFireOnUse(int iItemDamage) {
        return false;
    }

    @Override
    public int getFurnaceBurnTime(int iItemDamage) {
        return defaultFurnaceBurnTime;
    }
}
