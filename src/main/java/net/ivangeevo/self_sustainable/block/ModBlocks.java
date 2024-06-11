package net.ivangeevo.self_sustainable.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.*;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static net.minecraft.state.property.Properties.LIT;


public class ModBlocks
{

    public static final Block TORCH_UNLIT = registerBlockWithoutItem("torch_unlit",
            new ModTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.UNLIT, () -> 48000) );

    public static final Block TORCH_LIT = registerBlockWithoutItem("torch_lit",
            new ModTorchBlock( initTorchSettings().luminance(state -> 14),
                    ParticleTypes.FLAME, TorchFireState.LIT, () -> 48000) );

    public static final Block TORCH_SMOULDER = registerBlockWithoutItem("torch_smoulder",
            new ModTorchBlock( initTorchSettings().luminance(state -> 3),
                    ParticleTypes.FLAME, TorchFireState.SMOULDER, () -> 48000) );

    public static final Block TORCH_BURNED_OUT = registerBlockWithoutItem("torch_burned_out",
            new ModTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.BURNED_OUT, () -> 48000) );



    public static final Block WALL_TORCH_UNLIT = registerBlockWithoutItem("wall_torch_unlit",
            new ModWallTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.UNLIT, () -> 48000) );

    public static final Block WALL_TORCH_LIT = registerBlockWithoutItem("wall_torch_lit",
            new ModWallTorchBlock( initTorchSettings().luminance(state -> 14),
                    ParticleTypes.FLAME, TorchFireState.LIT, () -> 48000) );

    public static final Block WALL_TORCH_SMOULDER = registerBlockWithoutItem("wall_torch_smoulder",
            new ModWallTorchBlock( initTorchSettings().luminance(state -> 3),
                    ParticleTypes.SMOKE, TorchFireState.SMOULDER, () -> 48000) );

    public static final Block WALL_TORCH_BURNED_OUT = registerBlockWithoutItem("wall_torch_burned_out",
            new ModWallTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.BURNED_OUT, () -> 48000) );


    public static final Block OVEN_BRICK = registerBlock("oven_brick",
            new BrickOvenBlock(FabricBlockSettings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));
    public static final Block SMOKER_BRICK = registerBlock("smoker_brick",
            new SmokerOvenBlock(FabricBlockSettings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));

    private static FabricBlockSettings initTorchSettings()
    {
       return FabricBlockSettings.create()
                .noCollision()
                .breakInstantly()
                .pistonBehavior(PistonBehavior.DESTROY)
                .sounds(BlockSoundGroup.WOOD);
    }


   public static ModTorchHandler torches = new ModTorchHandler("basic");

    public static void registerTorchHandler()
    {

        torches.addTorch(ModBlocks.TORCH_LIT);
        torches.addTorch(ModBlocks.TORCH_UNLIT);
        torches.addTorch(ModBlocks.TORCH_SMOULDER);
        torches.addTorch(ModBlocks.TORCH_BURNED_OUT);
        torches.addTorch(ModBlocks.WALL_TORCH_LIT);
        torches.addTorch(ModBlocks.WALL_TORCH_UNLIT);
        torches.addTorch(ModBlocks.WALL_TORCH_SMOULDER);
        torches.addTorch(ModBlocks.WALL_TORCH_BURNED_OUT);


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
