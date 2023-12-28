package ivangeevo.selfsustainable.entity;

import ivangeevo.selfsustainable.block.ModBlocks;
import ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import ivangeevo.selfsustainable.block.entity.StoneOvenBlockEntity;
import ivangeevo.selfsustainable.block.entity.UnfiredBrickBlockEntity;
import ivangeevo.selfsustainable.SelfSustainableMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {
    public static BlockEntityType<UnfiredBrickBlockEntity> UNFIRED_BRICK_BLOCK;

    public static class Blocks {

        public static BlockEntityType<StoneOvenBlockEntity> OVEN_STONE;
        public static BlockEntityType<BrickOvenBlockEntity> OVEN_BRICK;


        public static void registerBlockEntities() {
            OVEN_STONE = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID, "oven_stone"),
                    FabricBlockEntityTypeBuilder.create(StoneOvenBlockEntity::new,
                            ModBlocks.OVEN_STONE).build(null));

            OVEN_BRICK = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(SelfSustainableMod.MOD_ID, "oven_brick"),
                    FabricBlockEntityTypeBuilder.create(BrickOvenBlockEntity::new,
                            ModBlocks.OVEN_BRICK).build(null));

        }
    }



}
