package net.ivangeevo.self_sustainable.item;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.items.*;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModItems
{

    public static final Item SS_GROUP = registerItem( "ss_group", new Item(new Item.Settings()));

    public static final Item FIRESTARTER_STICKS = registerItem("firestarter_sticks",
            new FireStarterItemPrimitive(new Item.Settings().maxDamage(250), 0.05F, -0.1F, 0.1F, 0.001F));
    public static final Item FIRESTARTER_BOW = registerItem("firestarter_bow",
            new FireStarterItemPrimitive(new Item.Settings().maxDamage(250),0.025F, -0.1F, 0.1F, 0.004F));

    public static final Item BRICK_UNFIRED = registerItem( "brick_unfired",
            new AliasedBlockItem(ModBlocks.BRICK_UNFIRED, new Item.Settings()));


    // All items below are unused for now
    public static final Item WOOL = registerItem("wool", new WoolItem( new Item.Settings().maxCount(64)));
    public static final Item WOOL_KNIT = registerItem( "wool_knit", new WoolKnitItem(new Item.Settings()));

    public static final Item KNITTING_NEEDLES = registerItem("knitting_needles",
            new KnittingNeedlesItem(ToolMaterials.WOOD,
                    new Item.Settings().recipeRemainder(ModItems.KNITTING_NEEDLES)));

    public static final Item KNITTING = registerItem("knitting",
            new KnittingItem(new Item.Settings()));

    public static final Item WICKER = registerItem("wicker", new Item(new Item.Settings()));
    public static final Item WICKER_WEAVING = registerItem("wicker_weaving",
            new WickerWeavingItem(new Item.Settings().maxCount(1)));

    public static final Item CRUDE_TORCH_UNLIT = registerItem("crude_torch_unlit", new CrudeTorchItem(ModBlocks.CRUDE_TORCH_UNLIT, ModBlocks.CRUDE_WALL_TORCH_UNLIT, new Item.Settings(), TorchFireState.UNLIT, ModBlocks.crudeTorches));
    public static final Item CRUDE_TORCH_LIT = registerItem("crude_torch_lit", new CrudeTorchItem(ModBlocks.CRUDE_TORCH_LIT, ModBlocks.CRUDE_WALL_TORCH_LIT, new Item.Settings().maxDamage(24000), TorchFireState.LIT, ModBlocks.crudeTorches));
    public static final Item CRUDE_TORCH_SMOULDER = registerItem("crude_torch_smoulder", new CrudeTorchItem(ModBlocks.CRUDE_TORCH_SMOULDER, ModBlocks.CRUDE_WALL_TORCH_SMOULDER, new Item.Settings().maxDamage(24000), TorchFireState.SMOULDER, ModBlocks.crudeTorches));
    public static final Item CRUDE_TORCH_BURNED_OUT = registerItem("crude_torch_burned_out", new CrudeTorchItem(ModBlocks.CRUDE_TORCH_BURNED_OUT, ModBlocks.CRUDE_WALL_TORCH_BURNED_OUT, new Item.Settings().maxCount(1), TorchFireState.BURNED_OUT, ModBlocks.crudeTorches));

    public static final Item TORCH_UNLIT = registerItem("torch_unlit", new VerticallyAttachableBlockItem(ModBlocks.TORCH_UNLIT, ModBlocks.WALL_TORCH_UNLIT, new Item.Settings(), Direction.DOWN));



    // ****************** //

    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, Identifier.of(SelfSustainableMod.MOD_ID, name), item);
    }

    public static void registerModItems()
    {
        SelfSustainableMod.LOGGER.info("Registering Mod Items for " + SelfSustainableMod.MOD_ID);
    }
}
