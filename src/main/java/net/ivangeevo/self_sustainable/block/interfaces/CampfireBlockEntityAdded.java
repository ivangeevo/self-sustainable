package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CampfireBlockEntityAdded
{

    int getLitTime();

    void setLitTime(int value);

    void setCookTime(int value);
    int getCookTime();

    void setTotalCookTime(int value);
    int getTotalCookTime();

    ItemStack getCookStack();
    void setCookStack(ItemStack newStack);

    void retrieveItem(World world, CampfireBlockEntity campfireBE, PlayerEntity player);

    void setSpitStack(ItemStack stack);

    ItemStack getSpitStack();

    void addBurnTime(BlockState state, int iBurnTime);

    void changeFireLevel(BlockState state, int iFireLevel);

    void onFirstLit();


}
