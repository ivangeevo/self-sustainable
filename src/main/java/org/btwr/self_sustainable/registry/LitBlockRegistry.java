package org.btwr.self_sustainable.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock;
import org.btwr.self_sustainable.block.utils.CampfireState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

public final class LitBlockRegistry {

    /** Blocks → lit property checks */
    private static final Map<Block, CopyOnWriteArrayList<PropertyCheck<?>>> LIT_ENTRIES =
            new ConcurrentHashMap<>();

    /** Optional override for full lit-state logic */
    private static final AtomicReference<Predicate<BlockState>> RUNTIME_CHECKER =
            new AtomicReference<>(LitBlockRegistry::defaultLitCheck);

    private LitBlockRegistry() {}

    // ------------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------------

    /** True if block is lit according to registered rules or fallback heuristics. */
    public static boolean isLit(BlockState state) {
        try {
            return RUNTIME_CHECKER.get().test(state);
        } catch (Throwable t) {
            return defaultLitCheck(state);
        }
    }

    // TODO: Not sure if we even need this
    /**
     * True ONLY if the registry defines conditions for this block
     * AND those conditions evaluate to "not lit".
     *
     * If no conditions exist for this block, this returns false.
     */
    public static boolean isUnlit(BlockState state) {
        Block block = state.getBlock();

        List<PropertyCheck<?>> checks = LIT_ENTRIES.get(block);
        if (checks == null || checks.isEmpty()) {
            return false; // unlit is only meaningful if we have lit rules
        }

        return !isLit(state);
    }

    /** Register what should be considered LIT for a block. */
    public static <T extends Comparable<T>> void registerLit(
            Block block,
            Property<T> property,
            Predicate<T> predicate) {

        LIT_ENTRIES.computeIfAbsent(block, b -> new CopyOnWriteArrayList<>())
                .add(new PropertyCheck<>(property, predicate));
    }

    /** Replace entire lit-evaluator. Usually unnecessary. */
    public static void setRuntimeChecker(Predicate<BlockState> checker) {
        RUNTIME_CHECKER.set(Objects.requireNonNull(checker));
    }

    // ------------------------------------------------------------------------
    // Default lit behavior
    // ------------------------------------------------------------------------

    private static boolean defaultLitCheck(BlockState state) {
        List<PropertyCheck<?>> checks = LIT_ENTRIES.get(state.getBlock());

        if (checks != null && !checks.isEmpty()) {
            for (PropertyCheck<?> c : checks) {
                if (!c.matches(state)) return false;
            }
            return true;
        }

        // Fallbacks only for lit (not used for unlit)
        if (state.contains(Properties.LIT)) {
            return state.get(Properties.LIT);
        }

        if (state.contains(IVariableCampfireBlock.FIRE_LEVEL)) {
            return state.get(IVariableCampfireBlock.FIRE_LEVEL) > 0;
        }

        return false;
    }

    // ------------------------------------------------------------------------
    // Defaults
    // ------------------------------------------------------------------------

    public static void registerDefaults() {
        registerLit(ModBlocks.OVEN_BRICK, BrickOvenBlock.FUEL_LEVEL, v -> v > 0);
        registerLit(ModBlocks.OVEN_BRICK, Properties.LIT, v -> v);

        registerLit(Blocks.CAMPFIRE, IVariableCampfireBlock.FIRE_LEVEL, v -> v > 0);
        registerLit(Blocks.CAMPFIRE, IVariableCampfireBlock.FUEL_STATE, v -> v != CampfireState.BURNED_OUT);
    }

    // ------------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------------

    public static final class PropertyCheck<T extends Comparable<T>> {
        private final Property<T> property;
        private final Predicate<T> predicate;

        public PropertyCheck(Property<T> property, Predicate<T> predicate) {
            this.property = property;
            this.predicate = predicate;
        }

        @SuppressWarnings("unchecked")
        public boolean matches(BlockState state) {
            if (!state.contains(property)) return false;
            T v = state.get((Property<T>) property);
            return predicate.test(v);
        }
    }
}
