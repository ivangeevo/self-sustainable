package net.ivangeevo.self_sustainable.item.interfaces;

public interface ItemStackAdded
{

    long getTimeOfLastUse();


     void setTimeOfLastUse(long lTime);


     float getAccumulatedChance(float fDefault);


     void setAccumulatedChance(float fChance);
}
