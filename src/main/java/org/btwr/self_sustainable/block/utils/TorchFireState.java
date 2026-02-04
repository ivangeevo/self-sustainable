package org.btwr.self_sustainable.block.utils;

import net.minecraft.util.StringIdentifiable;

public enum TorchFireState implements StringIdentifiable {
    UNLIT("unlit"),
    LIT("lit"),
    SMOULDER("smoulder"),
    BURNED_OUT("burned_out");

    private final String name;

    TorchFireState(String name) {
        this.name = name;
    }

    @Override public String asString() {
        return this.name;
    }

    @Override public String toString() {
        return this.name;
    }

}