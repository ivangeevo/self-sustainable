package net.ivangeevo.self_sustainable;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.item.items.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

public class ModItems {

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
    public static final Item CRUDE_TORCH = registerItem("crude_torch",
            new CrudeTorchItem(ModBlocks.CRUDE_TORCH, ModBlocks.WALL_CRUDE_TORCH,
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
