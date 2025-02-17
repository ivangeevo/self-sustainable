package net.ivangeevo.self_sustainable.loot;

import com.mojang.serialization.MapCodec;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModLootFunctionTypes {

    public static LootFunctionType<SetDamageTorchLootFunction> SET_DAMAGE_TORCH;

    public static void register() {
        SET_DAMAGE_TORCH = register("set_damage_torch", SetDamageTorchLootFunction.CODEC);
    }

    private static <T extends LootFunction> LootFunctionType<T> register(String id, MapCodec<T> codec) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, Identifier.of(SelfSustainableMod.MOD_ID, id), new LootFunctionType<>(codec));
    }

}
