package org.btwr.self_sustainable.entity.interfaces.added;

public interface LivingEntityAdded {

    default void btwr$setItemUseTime(int iCount) {
        throw new UnsupportedOperationException();
    }

}