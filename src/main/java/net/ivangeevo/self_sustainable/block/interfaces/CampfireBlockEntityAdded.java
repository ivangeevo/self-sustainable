package net.ivangeevo.self_sustainable.block.interfaces;

import net.ivangeevo.self_sustainable.block.VariableCampfireBE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CampfireBlockEntityAdded
{

    void setCookTime(int value);
    int getCookTime();

    void setBurnTime(int value);
    void setBurnTimeCountdown(int value);

    int getBurnTime();
    int getBurnTimeCountdown();


    void setTotalCookTime(int value);
    int getTotalCookTime();

    ItemStack getCookStack();
    void setCookStack(ItemStack newStack);

    void retrieveItem(World world, VariableCampfireBE campfireBE, PlayerEntity player);

    void setSpitStack(ItemStack stack);

    ItemStack getSpitStack();

    void addBurnTime(BlockState state, int iBurnTime);

    void changeFireLevel(int iFireLevel);

    void onFirstLit();


}
