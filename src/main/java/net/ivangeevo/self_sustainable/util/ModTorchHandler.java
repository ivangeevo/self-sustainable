package net.ivangeevo.self_sustainable.util;

import net.ivangeevo.self_sustainable.block.blocks.ModTorchBlock;
import net.ivangeevo.self_sustainable.block.blocks.ModWallTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.WallTorchBlock;

import java.util.EnumMap;
import java.util.Map;

public class ModTorchHandler
{

    // TODO: The torch classes defined in the 2nd parameter of these maps & the rest of the class
    //  should actually be new classes with custom block functionality added, like extinguishing in water, in rain etc...

    private final Map<TorchFireState, ModTorchBlock> standingTorches = new EnumMap<>(TorchFireState.class);
    private final Map<TorchFireState, ModWallTorchBlock> wallTorches = new EnumMap<>(TorchFireState.class);
    public final String name;

    public ModTorchHandler(String name) {
        this.name = name;
    }

    public void addTorch(Block block) {
        if (block instanceof ModTorchBlock standingTorch)
        {
            addStandingTorch(standingTorch);
        }
        else if (block instanceof ModWallTorchBlock wallTorch)
        {
            addWallTorch(wallTorch);
        }
    }

    private void addStandingTorch(ModTorchBlock block)
    {
        standingTorches.put(block.fireState, block);
        block.handler = this;
    }

    private void addWallTorch(ModWallTorchBlock block)
    {
        wallTorches.put(block.fireState, block);
        block.handler = this;
    }

    public ModTorchBlock getStandingTorch(TorchFireState state)
    {
        return standingTorches.get(state);
    }
    public ModWallTorchBlock getWallTorch(TorchFireState state)
    {
        return wallTorches.get(state);
    }
}
