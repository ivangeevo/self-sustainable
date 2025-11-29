package org.btwr.self_sustainable.item.interfaces.added;

import org.btwr.self_sustainable.util.CustomUseAction;

public interface ItemStackAdded {

    default long btwr$getTimeOfLastUse() {
        throw new UnsupportedOperationException();
    }

    default void btwr$setTimeOfLastUse(long lTime) {
        throw new UnsupportedOperationException();
    }

    default float btwr$getAccumulatedChance(float fDefault){
        throw new UnsupportedOperationException();
    }

    default void btwr$setAccumulatedChance(float fChance) {
        throw new UnsupportedOperationException();
    }

    default CustomUseAction btwr$getCustomUseAction() {
        throw new UnsupportedOperationException();
    }

}