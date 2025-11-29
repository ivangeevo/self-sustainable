package org.btwr.self_sustainable.entity.interfaces.added;

import net.minecraft.entity.player.PlayerEntity;

public interface AnimalEntityAdded {

     default void btwr$onNearbyFireStartAttempt(PlayerEntity player) {
          throw new UnsupportedOperationException();
     }

}