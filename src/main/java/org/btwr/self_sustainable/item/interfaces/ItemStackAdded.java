package org.btwr.self_sustainable.item.interfaces;

import org.btwr.self_sustainable.util.CustomUseAction;

public interface ItemStackAdded {

    long getTimeOfLastUse();

    void setTimeOfLastUse(long lTime);

    float getAccumulatedChance(float fDefault);

    void setAccumulatedChance(float fChance);

    CustomUseAction getCustomUseAction();

}