package net.ivangeevo.self_sustainable.entity;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.VariableCampfireBE;
import net.ivangeevo.self_sustainable.block.entity.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<BrickOvenBE> OVEN_BRICK;
    public static BlockEntityType<SmokerOvenBE> SMOKER_BRICK;
    public static BlockEntityType<VariableCampfireBE> CAMPFIRE;
    public static BlockEntityType<UnfiredBrickBE> BRICK_UNFIRED;
    public static BlockEntityType<TorchBE> TORCH;

    private static final Block[] torchesArray = new Block[] {
        ModBlocks.CRUDE_TORCH_LIT,
                ModBlocks.CRUDE_TORCH_UNLIT,
                ModBlocks.CRUDE_TORCH_SMOULDER,
                ModBlocks.CRUDE_TORCH_BURNED_OUT,
                ModBlocks.CRUDE_WALL_TORCH_LIT,
                ModBlocks.CRUDE_WALL_TORCH_UNLIT,
                ModBlocks.CRUDE_WALL_TORCH_SMOULDER,
                ModBlocks.CRUDE_WALL_TORCH_BURNED_OUT
    };

    public static void registerBlockEntities() {

        OVEN_BRICK = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "oven_brick"),
                BlockEntityType.Builder.create(BrickOvenBE::new, ModBlocks.OVEN_BRICK).build(null)
        );

        SMOKER_BRICK = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "smoker_brick"),
                BlockEntityType.Builder.create(SmokerOvenBE::new,
                ModBlocks.SMOKER_BRICK).build(null)
        );

        CAMPFIRE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "campfire"),
                BlockEntityType.Builder.create(VariableCampfireBE::new, Blocks.CAMPFIRE).build(null)
        );

        BRICK_UNFIRED = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "brick_unfired"),
                BlockEntityType.Builder.create(UnfiredBrickBE::new, ModBlocks.BRICK_UNFIRED).build()
        );

        TORCH = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "torch"),
                BlockEntityType.Builder.create(TorchBE::new, torchesArray).build()
        );

        /**
        CRUDE_TORCH = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "crude_torch"),
                BlockEntityType.Builder.create(CrudeTorchBlockEntity::new, ModBlocks.CRUDE_TORCH, ModBlocks.WALL_CRUDE_TORCH).build(null)
        );
         **/

        }


}
