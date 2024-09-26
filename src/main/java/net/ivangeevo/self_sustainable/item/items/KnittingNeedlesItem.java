package net.ivangeevo.self_sustainable.item.items;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.ToolMaterials;
import net.minecraft.world.World;

public class KnittingNeedlesItem extends Item
{

    private final ToolMaterials material;

    public KnittingNeedlesItem(ToolMaterials material, Item.Settings group) {
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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
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
    public boolean getCanItemStartFireOnUse(ItemStack stack) {
        return true;
    }

    @Override
    public boolean getCanItemBeSetOnFireOnUse(ItemStack stack) {
        return true;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoCampfire(ItemStack stack) {
        return false;
    }

    @Override
    public boolean getCanBeFedDirectlyIntoBrickOven(ItemStack stack) {
        return false;
    }


    @Override
    public boolean isEfficientVsBlock(ItemStack stack, World world, BlockState state) {
        return false;
    }
}
