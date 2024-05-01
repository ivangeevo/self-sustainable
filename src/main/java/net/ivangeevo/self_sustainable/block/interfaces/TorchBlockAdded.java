package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;

public interface TorchBlockAdded
{
    BooleanProperty LIT = Properties.LIT;

    void appendProperties(StateManager.Builder<Block, BlockState> builder);
}
