package net.ivangeevo.self_sustainable.mixin;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.CampfireBlockManager;
import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.ivangeevo.self_sustainable.util.CustomUseAction;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements ItemAdded
{

    @Override
    public CustomUseAction getCustomUseAction() {
        return CustomUseAction.NONE;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return !getCanItemBeSetOnFireOnUse(stack) && !getCanItemStartFireOnUse(stack);
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(ItemStack stack)
    {
        return !getCanItemBeSetOnFireOnUse(stack) && !getCanItemStartFireOnUse(stack) &&
                getCampfireBurnTime(stack) > 0;
    }

    @Override
    public int getCampfireBurnTime(ItemStack stack)
    {
        return CampfireBlockManager.getInstance().getItemFuelTime(stack);
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
