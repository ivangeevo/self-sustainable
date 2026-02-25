package org.btwr.self_sustainable.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;

/**
 * Adapted directly from Primitive Storage (CC0).
 *
 * <p>Original project:
 * <a href="https://github.com/jeffinitup/primitive-storage/">
 * https://github.com/jeffinitup/primitive-storage/
 * </a>
 *
 * <p>Original author:
 * JeffyJamzhd
 *
 */
public interface TickableBlockEntity {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getTicker() {
        return (world, pos, state, blockEntity) -> {
            if (blockEntity instanceof TickableBlockEntity tBE)
                tBE.tick();
        };
    }
}
