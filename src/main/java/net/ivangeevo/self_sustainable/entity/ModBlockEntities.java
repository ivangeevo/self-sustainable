package net.ivangeevo.self_sustainable.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.self_sustainable.block.entity.CrudeTorchBlockEntity;
import net.ivangeevo.self_sustainable.block.entity.TorchBlockEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {

    public static BlockEntityType<BrickOvenBlockEntity> OVEN_BRICK;

    public static BlockEntityType<TorchBlockEntity> TORCH;
    public static BlockEntityType<CrudeTorchBlockEntity> CRUDE_TORCH;


    public static void registerBlockEntities()
        {
            OVEN_BRICK = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                            "oven_brick"), FabricBlockEntityTypeBuilder.create(BrickOvenBlockEntity::new,
                            ModBlocks.OVEN_BRICK).build(null));

            TORCH = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "torch"), FabricBlockEntityTypeBuilder.create(TorchBlockEntity::new,
                    Blocks.TORCH).build(null));

            /**
            CRUDE_TORCH = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID,
                    "crude_torch"), FabricBlockEntityTypeBuilder.create(CrudeTorchBlockEntity::new,
                    ModBlocks.CRUDE_TORCH).build(null));
             **/

        }


}
