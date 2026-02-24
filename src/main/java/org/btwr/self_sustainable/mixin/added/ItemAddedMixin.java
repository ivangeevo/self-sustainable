package org.btwr.self_sustainable.mixin.added;

import org.btwr.self_sustainable.block.CampfireBlockMixinManager;
import org.btwr.self_sustainable.item.interfaces.added.ItemAdded;
import org.btwr.self_sustainable.tag.ModTags;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemAddedMixin implements ItemAdded {

    @Override
    public boolean btwr$getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return !btwr$getCanItemBeSetOnFireOnUse(stack) && !btwr$getCanItemStartFireOnUse(stack);
    }

    @Override
    public boolean btwr$getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        return !btwr$getCanItemBeSetOnFireOnUse(stack) && !btwr$getCanItemStartFireOnUse(stack) &&
                btwr$getCampfireBurnTime(stack) > 0;
    }

    @Override
    public int btwr$getCampfireBurnTime(ItemStack stack) {
        return CampfireBlockMixinManager.getInstance().getItemFuelTime(stack);
    }

    /** Default implementation simply uses any item in the tag **/
    @Override
    public boolean btwr$getCanItemBeSetOnFireOnUse(ItemStack stack) {
        return stack.isIn(ModTags.Items.CAN_BE_SET_ON_FIRE_ON_USE);
    }

    /** Default implementation simply uses any item in the tag **/
    @Override
    public boolean btwr$getCanItemStartFireOnUse(ItemStack stack) {
        return stack.isIn(ModTags.Items.CAN_START_FIRE_ON_USE);
    }

    @Override
    public int btwr$getOvenBurnTime(ItemStack stack) {
        return defaultFurnaceBurnTime;
    }

}