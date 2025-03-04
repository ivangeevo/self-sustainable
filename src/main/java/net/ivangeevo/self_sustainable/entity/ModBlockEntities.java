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
    public static BlockEntityType<BasketBlockEntity> WICKER_BASKET;
    public static BlockEntityType<HamperBlockEntity> HAMPER;

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
                Identifier.of(SelfSustainableMod.MOD_ID, "oven_brick_block_entity"),
                BlockEntityType.Builder.create(BrickOvenBE::new, ModBlocks.OVEN_BRICK).build(null)
        );

        SMOKER_BRICK = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "smoker_brick_block_entity"),
                BlockEntityType.Builder.create(SmokerOvenBE::new,
                        ModBlocks.SMOKER_BRICK).build(null)
        );

        CAMPFIRE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "campfire_block_entity"),
                BlockEntityType.Builder.create(VariableCampfireBE::new, Blocks.CAMPFIRE).build(null)
        );

        BRICK_UNFIRED = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "brick_unfired_block_entity"),
                BlockEntityType.Builder.create(UnfiredBrickBE::new, ModBlocks.BRICK_UNFIRED).build()
        );

        TORCH = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "torch_block_entity"),
                BlockEntityType.Builder.create(TorchBE::new, torchesArray).build()
        );

        WICKER_BASKET = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "wicker_basket_block_entity"),
                BlockEntityType.Builder.create(BasketBlockEntity::new, ModBlocks.WICKER_BASKET).build()
        );

        HAMPER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(SelfSustainableMod.MOD_ID, "hamper_block_entity"),
                BlockEntityType.Builder.create(HamperBlockEntity::new, ModBlocks.HAMPER).build()
        );

    }


}
