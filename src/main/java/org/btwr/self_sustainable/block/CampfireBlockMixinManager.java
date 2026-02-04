package org.btwr.self_sustainable.block;

import org.btwr.self_sustainable.block.interfaces.IVariableCampfireBlock;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import static net.minecraft.block.CampfireBlock.*;

public class CampfireBlockMixinManager implements IgnitableBlock, IVariableCampfireBlock {

    private static final CampfireBlockMixinManager instance = new CampfireBlockMixinManager();

    private CampfireBlockMixinManager() {}

    public static CampfireBlockMixinManager getInstance()
    {
        return instance;
    }

    public int getItemFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();

        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    public VoxelShape setCustomShapes(BlockState state) {
        if (!state.get(HAS_SPIT)) {
            return SHAPE;
        } else {
            return SHAPE_WITH_SPIT;
        }
    }

    public void appendCustomProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FUEL_STATE, FIRE_LEVEL, HAS_SPIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    public boolean getHasSpit(WorldAccess blockAccess, BlockPos pos) {
        return blockAccess.getBlockState(pos).get(HAS_SPIT);
    }

    public boolean setHasSpit(World world, BlockState state, BlockPos pos, boolean hasSpit) {
       return !world.isClient() && world.setBlockState(pos, state.with(HAS_SPIT, hasSpit));
    }

    private void playGetItemSound(World world, BlockPos pos, PlayerEntity player) {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                ( ( player.getRandom().nextFloat() - player.getRandom().nextFloat() ) * 0.7F + 1F ) * 2F);
    }

    private static boolean isLit(int fireLevel) {
        return fireLevel > 0;
    }

}