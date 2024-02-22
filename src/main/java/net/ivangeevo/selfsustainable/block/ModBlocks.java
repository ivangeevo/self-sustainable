package net.ivangeevo.selfsustainable.block;

import net.ivangeevo.selfsustainable.ModItemGroup;
import net.ivangeevo.selfsustainable.SelfSustainableMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.function.ToIntFunction;

public class ModBlocks {

    public static final Block OVEN_BRICK = registerBlock("oven_brick", new BrickOvenBlock(true,0,FabricBlockSettings.create().strength(1.5F,2.0F).luminance(setLightLevel(13)).sounds(BlockSoundGroup.STONE)), ModItemGroup.SS_GROUP);



    private static ToIntFunction<BlockState> setLightLevel(int litLevel) {
        return state -> state.get(Properties.LIT) ? litLevel : 0;
    }
    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Block registerBlock(String name, Block block, ItemGroup tab) {
        registerBlockItem(name, block, tab);
        return Registry.register(Registries.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block, ItemGroup tab) {
        return Registry.register(Registries.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        SelfSustainableMod.LOGGER.debug("Registering ModBlocks for " + SelfSustainableMod.MOD_ID);

    }

}
