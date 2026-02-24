package org.btwr.self_sustainable.material;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.item.ModItems;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SelfSustainableArmorMaterials {

    public static final RegistryEntry<ArmorMaterial> WOOL = register("wool",
            () -> new ArmorMaterial(
                    armorValues(1,1,1,1,2),
                    15,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
                    () -> Ingredient.ofItems(ModItems.WOOL_KNITS.values().toArray(new Item[0])),
                    List.of(
                            new ArmorMaterial.Layer(Identifier.of(SelfSustainableMod.MOD_ID, "wool"), "", true),
                            new ArmorMaterial.Layer(Identifier.of(SelfSustainableMod.MOD_ID, "wool"), "_overlay", false)
                    ),
                    0,
                    0
            )
    );

    private static Map<ArmorItem.Type, Integer> armorValues(int boots, int legs, int chest, int helmet, int body) {
      return Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, legs);
            map.put(ArmorItem.Type.CHESTPLATE, chest);
            map.put(ArmorItem.Type.HELMET, helmet);
            map.put(ArmorItem.Type.BODY, body);
        });
    }

    public static RegistryEntry<ArmorMaterial> register(String name, Supplier<ArmorMaterial> material) {
        return Registry.registerReference(Registries.ARMOR_MATERIAL, Identifier.of(SelfSustainableMod.MOD_ID, name), material.get());
    }
}