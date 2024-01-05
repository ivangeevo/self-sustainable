package ivangeevo.selfsustainable.block;

import ivangeevo.selfsustainable.ModItemGroup;
import ivangeevo.selfsustainable.SelfSustainableMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.function.ToIntFunction;

public class ModBlocks {

    public static final Block OVEN_STONE = registerBlock("oven_stone", new StoneOvenBlock(FabricBlockSettings.of(Material.STONE).strength(0.2F).luminance(setLightLevel(13)).sounds(BlockSoundGroup.STONE)), ModItemGroup.GROUP_BTWR);
    public static final Block OVEN_BRICK = registerBlock("oven_brick", new BrickOvenBlock(FabricBlockSettings.of(Material.STONE).strength(1.5F,2.0F).luminance(setLightLevel(13)).sounds(BlockSoundGroup.STONE)), ModItemGroup.GROUP_BTWR);


    private static ToIntFunction<BlockState> setLightLevel(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registry.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registry.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings().group(tab)));
    }

    public static void registerModBlocks() {
        SelfSustainableMod.LOGGER.debug("Registering ModBlocks for " + SelfSustainableMod.MOD_ID);
    }

}
