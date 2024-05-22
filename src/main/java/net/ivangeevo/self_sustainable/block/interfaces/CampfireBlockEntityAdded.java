package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface CampfireBlockEntityAdded
{

    int getLitTime();

    void setLitTime(int value);

    int setCookTime(int value);
    int getCookTime();

    int setTotalCookTime(int value);
    int getTotalCookTime();

    ItemStack getItemBeingCooked();
    void setItemBeingCooked(ItemStack newStack);

    ItemStack retrieveItem(@Nullable Entity user);




}
