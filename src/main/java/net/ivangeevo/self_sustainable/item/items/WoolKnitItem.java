package net.ivangeevo.self_sustainable.item.items;

import net.ivangeevo.self_sustainable.util.WoolColorsHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WoolKnitItem extends Item implements ItemColorProvider {
    public WoolKnitItem(Settings settings) {
        super(settings);

    }


    private static final Map<DyeColor, Integer> colorMap = new HashMap<>();

    static {
        colorMap.put(DyeColor.WHITE, 0xFFFFFF);
        colorMap.put(DyeColor.ORANGE, 0XEB8844);
        colorMap.put(DyeColor.MAGENTA, 0XBB44BB);
        colorMap.put(DyeColor.LIGHT_BLUE, 0X6699D8);
        colorMap.put(DyeColor.YELLOW, 0XDBD85A);
        colorMap.put(DyeColor.LIME, 0X6B8F35);
        colorMap.put(DyeColor.PINK, 0XF48B8B);
        colorMap.put(DyeColor.GRAY, 0X434343);
        colorMap.put(DyeColor.LIGHT_GRAY, 0X9E9E9E);
        colorMap.put(DyeColor.CYAN, 0X4B679D);
        colorMap.put(DyeColor.PURPLE, 0XAA47BC);
        colorMap.put(DyeColor.BLUE, 0X3C44AA);
        colorMap.put(DyeColor.BROWN, 0X835432);
        colorMap.put(DyeColor.GREEN, 0X5E7F3C);
        colorMap.put(DyeColor.RED, 0XB02E26);
        colorMap.put(DyeColor.BLACK, 0X1E1B1B);
    }

    /**
    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> list) {
        for (DyeColor color : DyeColor.values()){
            list.add( new ItemStack(this, 1));
           // setWoolItemTooltip(stack);
        }
    }
    **/



    //------------- Class Specific Methods ------------//


    private void setWoolItemTooltip(ItemStack stack) {
        DyeableItem dyedItem = (DyeableItem) stack.getItem();
        DyeColor dyeColor = DyeColor.byId(dyedItem.getColor(stack));
        String woolType = WoolColorsHelper.woolColorNames[dyeColor.getId()];
        String name = String.format("%s Wool", woolType);
        stack.setCustomName(Text.of(name));
    }


    @Override
    public int getOvenBurnTime(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getCanItemStartFireOnUse(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        return false;
    }

    @Override
    public int getCampfireBurnTime(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state)
    {
        return false;
    }

    @Override
    public int getHerbivoreFoodValue(int iItemDamage) {
        return 0;
    }

    @Override
    public Item setHerbivoreFoodValue(int iFoodValue) {
        return null;
    }

    @Override
    public Item setAsBasicHerbivoreFood() {
        return null;
    }

    @Override
    public void updateUsingItem(ItemStack stack, World world, PlayerEntity player) {

    }

    @Override
    public int getItemUseWarmupDuration() {
        return 0;
    }


    //------------ Client Side Functionality ----------//





    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        return WoolItem.woolColors[stack.getDamage()];
    }

}
