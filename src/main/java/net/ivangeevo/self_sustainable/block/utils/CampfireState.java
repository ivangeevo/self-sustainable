package net.ivangeevo.self_sustainable.block.utils;

import net.minecraft.util.StringIdentifiable;

public enum CampfireState implements StringIdentifiable
{
    NORMAL("normal"),
    BURNED_OUT("burned_out"),
    SMOULDERING("smouldering");

    private final String name;

    CampfireState(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Assuming FUEL_STATE is the block state property representing the campfire state
    public static CampfireState convertToEnumState(int iCampfireState) {
        return switch (iCampfireState) {
            case 0 -> CampfireState.NORMAL;
            case 1 -> CampfireState.BURNED_OUT;
            case 2 -> CampfireState.SMOULDERING;
            default -> throw new IllegalArgumentException("Invalid campfire state: " + iCampfireState);
        };
    }
}
