package net.ivangeevo.self_sustainable.item;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.items.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{

    public static final Item SS_GROUP = registerItem( "ss_group", new Item(new Item.Settings()));

    public static final Item FIRESTARTER_STICKS = registerItem("firestarter_sticks",
            new FireStarterItemPrimitive(new Item.Settings().maxDamage(250), 0.05F, -0.1F, 0.1F, 0.001F));
    public static final Item FIRESTARTER_BOW = registerItem("firestarter_bow",
            new FireStarterItemPrimitive(new Item.Settings().maxDamage(250),0.025F, -0.1F, 0.1F, 0.004F));


    // All items below are unused for now
    public static final Item WOOL = registerItem("wool", new WoolItem( new Item.Settings().maxCount(64)));
    public static final Item WOOL_KNIT = registerItem( "wool_knit", new WoolKnitItem(new Item.Settings()));

    public static final Item KNITTING_NEEDLES = registerItem("knitting_needles",
            new KnittingNeedlesItem(ToolMaterials.WOOD,
                    new Item.Settings().recipeRemainder(ModItems.KNITTING_NEEDLES)));
    public static final Item KNITTING = registerItem("knitting",
            new KnittingItem(new Item.Settings().maxDamage(0)));


    public static final Item WICKER = registerItem("wicker", new Item(new Item.Settings()));
    public static final Item WICKER_WEAVING = registerItem("wicker_weaving",
            new WickerWeavingItem(new Item.Settings().maxCount(1)));

    //public static final Item TORCH_UNLIT = registerItem("torch_unlit", new TorchItem(ModBlocks.TORCH_UNLIT, ModBlocks.WALL_TORCH_UNLIT, new FabricItemSettings(), TorchFireState.UNLIT,48000, ModBlocks.torches));
    //public static final Item TORCH_LIT = registerItem("torch_lit",new TorchItem(ModBlocks.TORCH_LIT, ModBlocks.WALL_TORCH_LIT, new FabricItemSettings(), TorchFireState.LIT,48000, ModBlocks.torches));
    //public static final Item TORCH_SMOULDER = registerItem("torch_smoulder",new TorchItem(ModBlocks.TORCH_SMOULDER, ModBlocks.WALL_TORCH_SMOULDER, new FabricItemSettings(), TorchFireState.SMOULDER,48000, ModBlocks.torches));
    //public static final Item TORCH_BURNED_OUT = registerItem("torch_burned_out",new TorchItem(ModBlocks.TORCH_BURNED_OUT, ModBlocks.WALL_TORCH_BURNED_OUT, new FabricItemSettings(), TorchFireState.BURNED_OUT,48000, ModBlocks.torches));




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
