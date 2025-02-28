package net.ivangeevo.self_sustainable.item.interfaces;

import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface ILightableTorchItem {

    void self_sustainable$receiveLitTorchStack(World world, PlayerEntity player, ItemStack stack, Hand hand);

    TorchFireState self_sustainable$getTorchItemFireState(ItemStack stack);
}
