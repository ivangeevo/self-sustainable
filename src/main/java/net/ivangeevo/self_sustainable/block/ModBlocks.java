package net.ivangeevo.self_sustainable.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static net.minecraft.state.property.Properties.LIT;


public class ModBlocks
{

    public static final Block OVEN_BRICK = registerBlock("oven_brick",
            new BrickOvenBlock(FabricBlockSettings.create().strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));
    /**
     public static final Block TORCH = registerBlockWithoutItem("torch",
     new ModTorchBlock(setTorchSettings(), ParticleTypes.FLAME));

     public static final Block WALL_TORCH = registerBlockWithoutItem("wall_torch",
     new ModWallTorchBlock(setTorchSettings().dropsLike(Blocks.TORCH), ParticleTypes.FLAME));
     **/


    /**
    public static final Block TORCH = registerBlockWithoutItem("torch", new ModTorchBlock());

    public static final Block TORCH_WALL = registerBlockWithoutItem("torch_wall", new ModWallTorchBlock());

    public static final Block CRUDE_TORCH = registerBlockWithoutItem("crude_torch", new CrudeTorchBlock());
    public static final Block WALL_CRUDE_TORCH = registerBlockWithoutItem("wall_crude_torch", new WallCrudeTorchBlock());
     **/


    private static FabricBlockSettings setTorchSettings()
    {
        return FabricBlockSettings.create().noCollision().breakInstantly()
                .luminance((state) -> state.get(LIT) ? 14 : 0).sounds(BlockSoundGroup.WOOD)
                .pistonBehavior(PistonBehavior.DESTROY);
    }

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Block registerBlockWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(SelfSustainableMod.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        SelfSustainableMod.LOGGER.debug("Registering ModBlocks for " + SelfSustainableMod.MOD_ID);

    }

}
