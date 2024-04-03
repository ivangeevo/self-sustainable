package net.ivangeevo.self_sustainable.item.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.ivangeevo.self_sustainable.item.interfaces.ItemAdded;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.world.World;

public class KnittingNeedlesItem extends Item
{

    private final ToolMaterials material;

    public KnittingNeedlesItem(ToolMaterials material, FabricItemSettings group) {
        super(group);
        this.material = material;
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {

        if (stack.getDamage() < stack.getMaxDamage() - 1) {
            ItemStack moreDamaged = stack.copy();
            moreDamaged.setDamage(stack.getDamage() + 1);
            return moreDamaged;
        }

        return ItemStack.EMPTY;

        }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        // stupid large so it's never actually hit in practice
        return 72000;
    }

    public ToolMaterial getMaterial() {
        return this.material;
    }

    @Override
    public int getEnchantability() {
        return this.material.getEnchantability();
    }


    @Override
    public boolean getCanItemStartFireOnUse(int iItemDamage) {
        return true;
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(int iItemDamage) {
        return true;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(int iItemDamage) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(int iItemDamage) {
        return false;
    }


    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }
}
