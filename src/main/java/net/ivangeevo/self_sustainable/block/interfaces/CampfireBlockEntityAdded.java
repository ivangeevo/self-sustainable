package net.ivangeevo.self_sustainable.block.interfaces;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface CampfireBlockEntityAdded
{

    int getLitTime();
    void setLitTime(int value);

}
