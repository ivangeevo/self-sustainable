package net.ivangeevo.self_sustainable.block;

import btwr.btwr_sl.lib.util.PlaceableAsBlock;
import btwr.core.block.BTWR_Blocks;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.BrickBlock;
import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.blocks.SmokerOvenBlock;
import net.ivangeevo.self_sustainable.block.blocks.UnfiredBrickBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import static net.minecraft.state.property.Properties.LIT;


public class ModBlocks
{

    /**
    public static final Block TORCH_UNLIT = registerBlockWithoutItem("torch_unlit",
            new ModTorchBlock(ParticleTypes.FLAME,
                    initTorchSettings(), TorchFireState.UNLIT, () -> 48000));

    public static final Block TORCH_LIT = registerBlockWithoutItem("torch_lit",
            new ModTorchBlock(ParticleTypes.FLAME, initTorchSettings().luminance(state -> 14), TorchFireState.LIT, () -> 48000) );

    public static final Block TORCH_SMOULDER = registerBlockWithoutItem("torch_smoulder",
            new ModTorchBlock(ParticleTypes.FLAME,
                    initTorchSettings().luminance(state -> 3), TorchFireState.SMOULDER, () -> 48000));

    public static final Block TORCH_BURNED_OUT = registerBlockWithoutItem("torch_burned_out",
            new ModTorchBlock(ParticleTypes.FLAME,
                    initTorchSettings(), TorchFireState.BURNED_OUT, () -> 48000));



    public static final Block WALL_TORCH_UNLIT = registerBlockWithoutItem("wall_torch_unlit",
            new ModWallModTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.UNLIT, () -> 48000) );

    public static final Block WALL_TORCH_LIT = registerBlockWithoutItem("wall_torch_lit",
            new ModWallModTorchBlock( initTorchSettings().luminance(state -> 14),
                    ParticleTypes.FLAME, TorchFireState.LIT, () -> 48000) );

    public static final Block WALL_TORCH_SMOULDER = registerBlockWithoutItem("wall_torch_smoulder",
            new ModWallModTorchBlock( initTorchSettings().luminance(state -> 3),
                    ParticleTypes.SMOKE, TorchFireState.SMOULDER, () -> 48000) );

    public static final Block WALL_TORCH_BURNED_OUT = registerBlockWithoutItem("wall_torch_burned_out",
            new ModWallModTorchBlock( initTorchSettings(),
                    ParticleTypes.FLAME, TorchFireState.BURNED_OUT, () -> 48000) );
     **/


    public static final Block OVEN_BRICK = registerBlock("oven_brick",
            new BrickOvenBlock(AbstractBlock.Settings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));
    public static final Block SMOKER_BRICK = registerBlock("smoker_brick",
            new SmokerOvenBlock(AbstractBlock.Settings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)));

    public static final Block BRICK_UNFIRED = registerWithoutItem("brick_unfired", new UnfiredBrickBlock(AbstractBlock.Settings.create().breakInstantly().nonOpaque().sounds(BlockSoundGroup.STONE)));
    public static final Block BRICK = registerWithoutItem("brick", new BrickBlock(AbstractBlock.Settings.create().breakInstantly().nonOpaque().sounds(BlockSoundGroup.STONE)));


    private static AbstractBlock.Settings initTorchSettings()
    {
       return AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.WOOD);
    }

    /**
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
     **/


    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, Identifier.of(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Block registerWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(SelfSustainableMod.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, Identifier.of(SelfSustainableMod.MOD_ID, name),
                new BlockItem(block, new Item.Settings()));
    }

    public static void registerModBlocks() {
        SelfSustainableMod.LOGGER.debug("Registering ModBlocks for " + SelfSustainableMod.MOD_ID);

    }

    // Vanilla items that don't have blocks by default; We associate a block with them and make it placeable
    public static void registerItemsPlaceableAsBlocks() {
        PlaceableAsBlock.getInstance().registerPlaceable(Items.BRICK, ModBlocks.BRICK);
    }

}
