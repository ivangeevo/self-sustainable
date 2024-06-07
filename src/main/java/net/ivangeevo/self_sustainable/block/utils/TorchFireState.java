package net.ivangeevo.self_sustainable.block.utils;

import net.minecraft.util.StringIdentifiable;

public enum TorchFireState implements StringIdentifiable
{
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

    // Assuming FUEL_STATE is the block state property representing the torch state
    public static TorchFireState convertToEnumState(int iCampfireState) {
        return switch (iCampfireState) {
            case 0 -> TorchFireState.UNLIT;
            case 1 -> TorchFireState.LIT;
            case 2 -> TorchFireState.SMOULDER;
            case 3 -> TorchFireState.BURNED_OUT;
            default -> throw new IllegalArgumentException("Invalid torch state: " + iCampfireState);
        };
    }
}
