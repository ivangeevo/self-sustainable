package net.ivangeevo.self_sustainable.block;

import btwr.btwr_sl.lib.util.PlaceableAsBlock;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.blocks.*;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.TorchBlock;
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


public class ModBlocks {

    public static final Block WICKER_BASKET = registerBlock("wicker_basket",
            new WickerBasketBlock(AbstractBlock.Settings.create()
                    .hardness(0.05F)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.GRASS)
            )
    );

    public static final Block HAMPER = registerBlock("hamper",
            new HamperBlock(AbstractBlock.Settings.create()
                    .hardness(0.05F)
                    .nonOpaque()
                    .sounds(BlockSoundGroup.GRASS)
            )
    );

    public static final Block OVEN_BRICK = registerBlock("oven_brick",
            new BrickOvenBlock(AbstractBlock.Settings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)
            )
    );

    public static final Block SMOKER_BRICK = registerBlock("smoker_brick",
            new SmokerOvenBlock(AbstractBlock.Settings.create()
                    .strength(1.5F,2.0F)
                    .luminance((state) -> state.get(LIT) ? 10 : 0)
                    .sounds(BlockSoundGroup.STONE)
            )
    );
    
    public static final Block CRUDE_TORCH_UNLIT = registerWithoutItem("crude_torch_unlit",
            new CrudeTorchBlock(ParticleTypes.FLAME, initTorchSettings(), TorchFireState.UNLIT));

    public static final Block CRUDE_TORCH_LIT = registerWithoutItem("crude_torch_lit",
            new CrudeTorchBlock(ParticleTypes.FLAME, initTorchSettings().luminance(state -> 14), TorchFireState.LIT));

    public static final Block CRUDE_TORCH_SMOULDER = registerWithoutItem("crude_torch_smoulder",
            new CrudeTorchBlock(ParticleTypes.FLAME, initTorchSettings().luminance(state -> 3), TorchFireState.SMOULDER));

    public static final Block CRUDE_TORCH_BURNED_OUT = registerWithoutItem("crude_torch_burned_out",
            new CrudeTorchBlock(ParticleTypes.FLAME, initTorchSettings(), TorchFireState.BURNED_OUT));

    public static final Block CRUDE_WALL_TORCH_UNLIT = registerWithoutItem("crude_wall_torch_unlit",
            new CrudeWallTorchBlock(initTorchSettings(), ParticleTypes.FLAME, TorchFireState.UNLIT));

    public static final Block CRUDE_WALL_TORCH_LIT = registerWithoutItem("crude_wall_torch_lit",
            new CrudeWallTorchBlock(initTorchSettings().luminance(state -> 14), ParticleTypes.FLAME, TorchFireState.LIT));

    public static final Block CRUDE_WALL_TORCH_SMOULDER = registerWithoutItem("crude_wall_torch_smoulder",
            new CrudeWallTorchBlock(initTorchSettings().luminance(state -> 3), ParticleTypes.SMOKE, TorchFireState.SMOULDER));

    public static final Block CRUDE_WALL_TORCH_BURNED_OUT = registerWithoutItem("crude_wall_torch_burned_out",
            new CrudeWallTorchBlock(initTorchSettings(), ParticleTypes.FLAME, TorchFireState.BURNED_OUT));

    public static final Block TORCH_UNLIT = registerWithoutItem("torch_unlit",
            new TorchBlock(ParticleTypes.FLAME, initTorchSettings()));

    public static final Block WALL_TORCH_UNLIT = registerWithoutItem("wall_torch_unlit",
            new WallTorchBlock(ParticleTypes.FLAME, initTorchSettings()));


    public static final Block BRICK_UNFIRED = registerWithoutItem("brick_unfired",
            new UnfiredBrickBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.STONE)
            )
    );

    public static final Block BRICK = registerWithoutItem("brick",
            new BrickBlock(AbstractBlock.Settings.create()
                    .breakInstantly()
                    .nonOpaque()
                    .sounds(BlockSoundGroup.STONE)
            )
    );


    private static AbstractBlock.Settings initTorchSettings() {
       return AbstractBlock.Settings.create().noCollision().breakInstantly().pistonBehavior(PistonBehavior.DESTROY).sounds(BlockSoundGroup.WOOD);
    }

   public static ModTorchHandler crudeTorches = new ModTorchHandler("crude");
    public static ModTorchHandler torches = new ModTorchHandler("infinite");

    public static void registerTorchHandler() {
        crudeTorches.addTorch(ModBlocks.CRUDE_TORCH_LIT);
        crudeTorches.addTorch(ModBlocks.CRUDE_TORCH_UNLIT);
        crudeTorches.addTorch(ModBlocks.CRUDE_TORCH_SMOULDER);
        crudeTorches.addTorch(ModBlocks.CRUDE_TORCH_BURNED_OUT);
        crudeTorches.addTorch(ModBlocks.CRUDE_WALL_TORCH_LIT);
        crudeTorches.addTorch(ModBlocks.CRUDE_WALL_TORCH_UNLIT);
        crudeTorches.addTorch(ModBlocks.CRUDE_WALL_TORCH_SMOULDER);
        crudeTorches.addTorch(ModBlocks.CRUDE_WALL_TORCH_BURNED_OUT);

        torches.addTorch(ModBlocks.TORCH_UNLIT);
        torches.addTorch(ModBlocks.WALL_TORCH_UNLIT);
    }

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
