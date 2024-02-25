package net.ivangeevo.selfsustainable.block.blocks;

import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class OvenFuelLevelBlock extends Block {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public static final IntProperty FUEL_LEVEL = BrickOvenBlock.FUEL_LEVEL;

    public OvenFuelLevelBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }



    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext context) {
        return this.getDefaultState().with(FACING, context.getPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

                // Set the fuel level property based on the associated BrickOvenBlockEntity
                BlockEntity ovenBlockEntity = world.getBlockEntity(pos.offset(state.get(FACING)));
                if (ovenBlockEntity instanceof BrickOvenBlockEntity ovenEntity) {
                    ovenEntity.updateFuelLevel(ovenEntity.getVisualFuelLevel());
                }
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, FUEL_LEVEL);
    }
}
