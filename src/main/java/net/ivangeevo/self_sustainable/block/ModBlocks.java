package net.ivangeevo.self_sustainable.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock.LIT;

public class ModBlocks {

    public static final Block OVEN_BRICK = registerBlock("oven_brick",
            new BrickOvenBlock(FabricBlockSettings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));

/**
    public static final Block CRUDE_TORCH = registerBlockWithoutItem("crude_torch",
            new CrudeTorchBlock(FabricBlockSettings.create()
                    .noCollision()
                    .breakInstantly()
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.WOOD)
                    .pistonBehavior(PistonBehavior.DESTROY), ParticleTypes.FLAME));

    public static final Block TORCH = registerBlockWithoutItem("torch",
            new ModTorchBlock(FabricBlockSettings.create()
                    .noCollision()
                    .breakInstantly()
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.WOOD)
                    .pistonBehavior(PistonBehavior.DESTROY), ParticleTypes.FLAME));

    public static final Block WALL_CRUDE_TORCH = registerBlockWithoutItem("wall_crude_torch",
            new WallCrudeTorchBlock(FabricBlockSettings.create()
                    .noCollision()
                    .breakInstantly()
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.WOOD)
                    .dropsLike(CRUDE_TORCH)
                    .pistonBehavior(PistonBehavior.DESTROY), ParticleTypes.FLAME));
    public static final Block WALL_TORCH = registerBlockWithoutItem("wall_torch",
            new ModWallTorchBlock(FabricBlockSettings.create()
                    .noCollision()
                    .breakInstantly()
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.WOOD)
                    .dropsLike(TORCH)
                    .pistonBehavior(PistonBehavior.DESTROY), ParticleTypes.FLAME));
 **/





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
