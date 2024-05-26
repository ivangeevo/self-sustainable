package net.ivangeevo.self_sustainable.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.items.*;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.state.property.Properties.LIT;

public class ModItems
{

    public static final Item SS_GROUP = registerItem( "ss_group", new Item(new FabricItemSettings()));

    public static final Item WOOL = registerItem("wool", new WoolItem( new FabricItemSettings().maxCount(64)));
    public static final Item WOOL_KNIT = registerItem( "wool_knit", new WoolKnitItem(new FabricItemSettings()));

    public static final Item KNITTING_NEEDLES = registerItem("knitting_needles",
            new KnittingNeedlesItem(ToolMaterials.WOOD,
                    new FabricItemSettings().maxDamage(8).recipeRemainder(ModItems.KNITTING_NEEDLES)));
    public static final Item KNITTING = registerItem("knitting",
            new KnittingItem(new FabricItemSettings().maxDamage(0)));

    /**
    public static final Item FIRESTARTER_STICKS = registerItem("firestarter_sticks",
            new FireStarterItemPrimitive(new FabricItemSettings().maxDamage(250), 0.05F, -0.1F, 0.1F, 0.001F));
    public static final Item FIRESTARTER_BOW = registerItem("firestarter_bow",
            new FireStarterItemPrimitive(new FabricItemSettings().maxDamage(250),0.025F, -0.1F, 0.1F, 0.004F));
     **/

    // Temporarily adding them as items only until I figure out Firestarter code.
    public static final Item FIRESTARTER_STICKS = registerItem("firestarter_sticks",
            new FlintAndSteelItem(new FabricItemSettings().maxDamage(1)));
    public static final Item FIRESTARTER_BOW = registerItem("firestarter_bow",
            new FlintAndSteelItem(new FabricItemSettings().maxDamage(8)));

    /**
    // There is only unlit torch item in here, the other is the vanilla one.
    // The blocks are vanilla torch and wall torch.
    public static final Item UNLIT_TORCH = registerItem("unlit_torch",
            new UnlitTorchItem( new FabricItemSettings(), Direction.DOWN) );

    public static final Item LIT_TORCH = registerItem("lit_torch", new LitTorchItem( new FabricItemSettings(), Direction.DOWN) );

    // The crude and crude unlit items get assigned to the modded torch blocks.
    public static final Item CRUDE_TORCH = registerItem("crude_torch",
            new CrudeTorchItem
                    (ModBlocks.CRUDE_TORCH.getDefaultState().with(LIT, true).getBlock(),
                    ModBlocks.WALL_CRUDE_TORCH.getDefaultState().with(LIT, true).getBlock(),
                    new FabricItemSettings(), Direction.DOWN));

    public static final Item CRUDE_TORCH_UNLIT = registerItem("crude_torch_unlit",
            new CrudeTorchItem(
                    ModBlocks.CRUDE_TORCH.getDefaultState().with(LIT, false).getBlock(),
                    ModBlocks.WALL_CRUDE_TORCH.getDefaultState().with(LIT, false).getBlock(),
                    new FabricItemSettings(), Direction.DOWN));
    **/




    private static Item registerItem(String name, Item item)
    {
        return Registry.register(Registries.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name), item);
    }

    public static void registerModItems()
    {
        SelfSustainableMod.LOGGER.info("Registering Mod Items for " + SelfSustainableMod.MOD_ID);

    }
}
