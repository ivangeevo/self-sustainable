package net.ivangeevo.self_sustainable.util;

import net.ivangeevo.self_sustainable.block.blocks.CrudeTorchBlock;
import net.ivangeevo.self_sustainable.block.blocks.CrudeWallTorchBlock;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.block.Block;

import java.util.EnumMap;
import java.util.Map;

public class ModTorchHandler {

    private final Map<TorchFireState, CrudeTorchBlock> standingTorches = new EnumMap<>(TorchFireState.class);
    private final Map<TorchFireState, CrudeWallTorchBlock> wallTorches = new EnumMap<>(TorchFireState.class);
    public final String name;

    public ModTorchHandler(String name) {
        this.name = name;
    }

    public void addTorch(Block block) {
        if (block instanceof CrudeTorchBlock standingTorch) {
            addStandingTorch(standingTorch);
        }
        else if (block instanceof CrudeWallTorchBlock wallTorch) {
            addWallTorch(wallTorch);
        }
    }

    private void addStandingTorch(CrudeTorchBlock block) {
        standingTorches.put(block.fireState, block);
        block.handler = this;
    }

    private void addWallTorch(CrudeWallTorchBlock block) {
        wallTorches.put(block.fireState, block);
        block.handler = this;
    }

    public CrudeTorchBlock getStandingTorch(TorchFireState state) {
        return standingTorches.get(state);
    }

    public CrudeWallTorchBlock getWallTorch(TorchFireState state) {
        return wallTorches.get(state);
    }

}