package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    ItemStack retrieveItem(@Nullable Entity user);

    void setSpitStack(ItemStack stack);

    ItemStack getSpitStack();

    void addBurnTime(BlockState state, int iBurnTime);

    void changeFireLevel(BlockState state, int iFireLevel);

    void onFirstLit();


}
