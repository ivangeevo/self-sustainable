package net.ivangeevo.self_sustainable.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.VariableCampfireBE;
import net.ivangeevo.self_sustainable.block.entity.*;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<BrickOvenBE> OVEN_BRICK;
    public static BlockEntityType<SmokerOvenBE> SMOKER_BRICK;
    public static BlockEntityType<VariableCampfireBE> CAMPFIRE;

    /**
    public static BlockEntityType<TorchBlockEntity> TORCH;
    public static BlockEntityType<CrudeTorchBlockEntity> CRUDE_TORCH;
     **/


    public static void registerBlockEntities()
        {
            OVEN_BRICK = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                            "oven_brick"), FabricBlockEntityTypeBuilder.create(BrickOvenBE::new,
                            ModBlocks.OVEN_BRICK).build(null));

            SMOKER_BRICK = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "smoker_brick"), FabricBlockEntityTypeBuilder.create(SmokerOvenBE::new,
                    ModBlocks.SMOKER_BRICK).build(null));

            CAMPFIRE = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "campfire"), FabricBlockEntityTypeBuilder.create(VariableCampfireBE::new,
                    Blocks.CAMPFIRE).build(null));

            /**
            TORCH = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "torch"), FabricBlockEntityTypeBuilder.create(TorchBlockEntity::new,
                    Blocks.TORCH, Blocks.WALL_TORCH).build(null));

            CRUDE_TORCH = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "crude_torch"), FabricBlockEntityTypeBuilder.create(CrudeTorchBlockEntity::new,
                    ModBlocks.CRUDE_TORCH, ModBlocks.WALL_CRUDE_TORCH).build(null));
             **/

        }


}
