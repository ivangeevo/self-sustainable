package net.ivangeevo.self_sustainable.util;

import net.ivangeevo.self_sustainable.block.utils.TorchFireState;

public interface ExtinguishableTorch {

    TorchFireState torchState = TorchFireState.UNLIT;

    boolean isEverlastingTorch();

    TorchFireState getTorchState();

    void setTorchState(TorchFireState fireState);


}
