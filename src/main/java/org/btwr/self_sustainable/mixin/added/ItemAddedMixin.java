package org.btwr.self_sustainable.mixin.added;

import org.btwr.self_sustainable.block.CampfireBlockMixinManager;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.interfaces.ItemAdded;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.self_sustainable.util.CustomUseAction;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemAddedMixin implements ItemAdded {

    @Override
    public CustomUseAction getCustomUseAction(ItemStack stack) {
        return stack.contains(ModComponentsTypes.PROGRESSIVE_CRAFTING) ? CustomUseAction.PROGRESSIVE_CRAFT : CustomUseAction.NONE;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return !getCanItemBeSetOnFireOnUse(stack) && !getCanItemStartFireOnUse(stack);
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        return !getCanItemBeSetOnFireOnUse(stack) && !getCanItemStartFireOnUse(stack) &&
                getCampfireBurnTime(stack) > 0;
    }

    @Override
    public int getCampfireBurnTime(ItemStack stack) {
        return CampfireBlockMixinManager.getInstance().getItemFuelTime(stack);
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(ItemStack stack) {
        return stack.isIn(ModTags.Items.CAN_BE_SET_ON_FIRE_ON_USE);
    }

    @Override
    public boolean getCanItemStartFireOnUse(ItemStack stack) {
        return stack.isIn(ModTags.Items.CAN_START_FIRE_ON_USE);
    }

    @Override
    public int getOvenBurnTime(ItemStack stack) {
        return defaultFurnaceBurnTime;
    }

    @Override
    public int getItemUseWarmupDuration() {
        return 7;
    }

}