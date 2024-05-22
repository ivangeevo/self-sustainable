package net.ivangeevo.self_sustainable.entity.util;

import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.minecraft.block.BlockEntityProvider;

/** A interface class for managing behavior of added entity capabilities
to torches. **/
public interface TorchEntityUtils extends BlockEntityProvider, Ignitable
{

    boolean lit = false;
    int litTime = 0;

}
