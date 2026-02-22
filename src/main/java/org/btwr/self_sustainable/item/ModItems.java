package org.btwr.self_sustainable.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.util.DyeColor;
import org.btwr.self_sustainable.SelfSustainableMod;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.ProgressiveCraftingComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.btwr.self_sustainable.item.items.*;
import org.btwr.self_sustainable.material.SelfSustainableArmorMaterials;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class ModItems {

    public static final Item SS_GROUP = registerItem( "ss_group", new Item(new Item.Settings()));

    public static final Item FIRESTARTER_STICKS = registerItem("firestarter_sticks",
            new FireStarterItemPrimitive(
                    new Item.Settings()
                            .maxDamage(250),
                    0.05F,
                    -0.1F,
                    0.1F,
                    0.001F)
    );
    public static final Item FIRESTARTER_BOW = registerItem("firestarter_bow",
            new FireStarterItemPrimitive(
                    new Item.Settings()
                            .maxDamage(250),
                    0.025F,
                    -0.1F,
                    0.1F,
                    0.004F)
    );

    public static final Item BRICK_UNFIRED = registerItem("brick_unfired",
            new AliasedBlockItem(ModBlocks.BRICK_UNFIRED, new Item.Settings())
    );

    public static final Item CRUDE_TORCH_UNLIT = registerItem(
            "crude_torch_unlit",
            new CrudeTorchBlockItem(
                    ModBlocks.CRUDE_TORCH_UNLIT,
                    ModBlocks.CRUDE_WALL_TORCH_UNLIT,
                    new Item.Settings(),
                    TorchFireState.UNLIT,
                    ModBlocks.crudeTorches)
    );
    public static final Item CRUDE_TORCH_LIT = registerItem(
            "crude_torch_lit",
            new CrudeTorchBlockItem(
                    ModBlocks.CRUDE_TORCH_LIT,
                    ModBlocks.CRUDE_WALL_TORCH_LIT,
                    new Item.Settings().maxDamage(24000),
                    TorchFireState.LIT,
                    ModBlocks.crudeTorches)
    );
    public static final Item CRUDE_TORCH_SMOULDER = registerItem(
            "crude_torch_smoulder",
            new CrudeTorchBlockItem(
                    ModBlocks.CRUDE_TORCH_SMOULDER,
                    ModBlocks.CRUDE_WALL_TORCH_SMOULDER,
                    new Item.Settings().maxDamage(24000),
                    TorchFireState.SMOULDER,
                    ModBlocks.crudeTorches)
    );
    public static final Item CRUDE_TORCH_BURNED_OUT = registerItem(
            "crude_torch_burned_out",
            new CrudeTorchBlockItem(
                    ModBlocks.CRUDE_TORCH_BURNED_OUT,
                    ModBlocks.CRUDE_WALL_TORCH_BURNED_OUT,
                    new Item.Settings().maxCount(1),
                    TorchFireState.BURNED_OUT,
                    ModBlocks.crudeTorches)
    );
    public static final Item TORCH_UNLIT = registerItem(
            "torch_unlit",
            new UnlitTorchBlockItem(
                    ModBlocks.TORCH_UNLIT,
                    ModBlocks.WALL_TORCH_UNLIT,
                    new Item.Settings(),
                    Direction.DOWN)
    );
    public static final Item SOUL_TORCH_UNLIT = registerItem(
            "soul_torch_unlit",
            new UnlitTorchBlockItem(
                    ModBlocks.SOUL_TORCH_UNLIT,
                    ModBlocks.SOUL_WALL_TORCH_UNLIT,
                    new Item.Settings(),
                    Direction.DOWN)
    );

    // All items below are unused for now
    //public static final Item WOOL = registerItem("wool", new WoolItem( new Item.Settings().maxCount(64)));
    //public static final Item KNITTING_NEEDLES = registerItem("knitting_needles", new KnittingNeedlesItem(new Item.Settings()));
    //public static final Item KNITTING = registerItem("knitting", new KnittingItem(new Item.Settings()));

    public static final Item WICKER = registerItem("wicker", new Item(new Item.Settings()));

    public static final Map<DyeColor, Item> WOOL_KNITS = new EnumMap<>(DyeColor.class);
    
    public static final Item WICKER_WEAVING = registerItem(
            "wicker_weaving",
            new WickerWeavingItem(
                    progressiveCraftingSettings(WickerWeavingItem.WICKER_WEAVING_MAX_DAMAGE, ModItems.WICKER)
            )
    );

    public static final Item WOOL_HELMET = registerItem("wool_helmet", new ArmorItem(
            SelfSustainableArmorMaterials.WOOL,
            ArmorItem.Type.HELMET,
            new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(1)))
    );
    public static final Item WOOL_CHESTPLATE = registerItem("wool_chestplate", new ArmorItem(
            SelfSustainableArmorMaterials.WOOL,
            ArmorItem.Type.CHESTPLATE,
            new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(1)))
    );
    public static final Item WOOL_LEGGINGS = registerItem("wool_leggings", new ArmorItem(
            SelfSustainableArmorMaterials.WOOL,
            ArmorItem.Type.LEGGINGS,
            new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(1)))
    );
    public static final Item WOOL_BOOTS = registerItem("wool_boots", new ArmorItem(
            SelfSustainableArmorMaterials.WOOL,
            ArmorItem.Type.BOOTS,
            new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(1)))
    );


    private static Item.Settings progressiveCraftingSettings(int maxDamage, Item result) {
        return new Item.Settings()
                .maxDamage(maxDamage)
                .component(ModComponentsTypes.PROGRESSIVE_CRAFTING, progressiveCraftingComponent(result));
    }

    private static ProgressiveCraftingComponent progressiveCraftingComponent(Item result) {
        return new ProgressiveCraftingComponent(Optional.of(result.getDefaultStack()));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SelfSustainableMod.MOD_ID, name), item);
    }

    public static void register() {
        SelfSustainableMod.LOGGER.info("Registering Mod Items for " + SelfSustainableMod.MOD_ID);

        for (DyeColor color : DyeColor.values()) {
            WOOL_KNITS.put(color, Registry.register(
                    Registries.ITEM,
                    Identifier.of(SelfSustainableMod.MOD_ID, color.getName() + "_wool_knit"),
                    new Item(new Item.Settings()
                            .component(DataComponentTypes.DYED_COLOR, new DyedColorComponent(0xFFFFFFFF, true)))
            ));
        }

    }

}