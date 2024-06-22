package net.ivangeevo.self_sustainable.item.interfaces;

import net.ivangeevo.self_sustainable.util.CustomUseAction;

public interface ItemStackAdded
{

    long getTimeOfLastUse();


     void setTimeOfLastUse(long lTime);


     float getAccumulatedChance(float fDefault);


     void setAccumulatedChance(float fChance);

}
