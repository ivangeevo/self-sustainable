package net.ivangeevo.selfsustainable.block.blocks;

import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.item.interfaces.ItemAdded;
import net.ivangeevo.selfsustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.selfsustainable.state.property.ModProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BrickOvenBlock extends BlockWithEntity {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public static final IntProperty FUEL_LEVEL = ModProperties.FUEL_LEVEL;

    protected final float clickYTopPortion = (6F / 16F);
    protected final float clickYBottomPortion = (6F / 16F);



    public BrickOvenBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(FACING, Direction.NORTH));
    }


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getMainHandStack();
        BrickOvenBlockEntity brickOvenBlockEntity;
        Optional<OvenCookingRecipe> optional;
        BlockEntity blockEntity = world.getBlockEntity(pos);


        double relativeClickY = hit.getPos().getY() - pos.getY();

        if (hit.getSide() != state.get(FACING))
        {
            return ActionResult.FAIL;
        }


        if (relativeClickY > clickYTopPortion)
        {
            if (blockEntity instanceof BrickOvenBlockEntity)
            {
                brickOvenBlockEntity = (BrickOvenBlockEntity) blockEntity;

                // Check for cooking
                if (!heldStack.isEmpty() && (optional = brickOvenBlockEntity.getRecipeFor(heldStack)).isPresent())
                {
                    if (!world.isClient && brickOvenBlockEntity.addItem(player, player.getAbilities().creativeMode ?
                            heldStack.copy() : heldStack, optional.get().getCookTime()))
                    {
                        return ActionResult.SUCCESS;
                    }
                } else {
                    // Check for retrieving
                    ItemStack retrievedItem = brickOvenBlockEntity.retrieveItem(player);
                    if (retrievedItem != null && !player.isCreative()) {
                        // Give the retrieved item to the player
                        if (!player.giveItemStack(retrievedItem)) {
                            // If the player couldn't receive the item, drop it at their feet
                            player.dropItem(retrievedItem, false);
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
        }
        else if (relativeClickY < clickYBottomPortion && heldStack != null)
        {

            // handle fuel here
            if (blockEntity instanceof BrickOvenBlockEntity) {
                brickOvenBlockEntity = (BrickOvenBlockEntity) blockEntity;

                Item item = heldStack.getItem();

                // if (((ItemAdded) item).getCanBeFedDirectlyIntoBrickOven(stackFuelAmount)) {
                if (!world.isClient) {

                    int iItemsConsumed = brickOvenBlockEntity.attemptToAddFuel(heldStack);

                    if (iItemsConsumed > 0) {

                        if (state.get(LIT)) {
                            this.playLitFX(world, pos);
                        } else {
                            this.playPopSound(world, pos);
                        }

                        if (!player.isCreative()) {
                            heldStack.decrement(iItemsConsumed);
                        }
                    }
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }


    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BrickOvenBlockEntity) {
            // Drops the contents when the block is destroyed
             ItemScatterer.spawn(world, pos, ((BrickOvenBlockEntity)blockEntity).getCookStack());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }

    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile)
    {
        BlockPos blockPos = hit.getBlockPos();

        double relativeClickY = hit.getPos().getY() - blockPos.getY();

        // Allow projectile interaction only from the facing side
        if (hit.getSide() == state.get(FACING) && this.isHavingFuel() && relativeClickY < clickYBottomPortion)
        {
            if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && !state.get(LIT))
            {
                world.setBlockState(blockPos, state.with(Properties.LIT, true),
                        Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

                this.playLitFX(world, blockPos);

            }


        }
    }

    private void playLitFX(World world, BlockPos pos) {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS,
                0.2F + world.random.nextFloat() * 0.1F, world.random.nextFloat() * 0.25F + 1.25F);

    }

    private void playPopSound(World world, BlockPos pos) {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS,
                0.25F, (world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F);

    }



    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation)
    {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror)
    {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, FACING, FUEL_LEVEL);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new BrickOvenBlockEntity(pos, state);
    }

    public boolean isHavingFuel () {
        BlockState state = this.getDefaultState();
        return FUEL_LEVEL.stream().spliterator().getExactSizeIfKnown() >= 0;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        if (world.isClient) {
            if (state.get(LIT))
            {
                return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::clientTick);
            }
        }
        else
        {
            if (state.get(LIT))
            {
                return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::litServerTick);
            }
            return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBlockEntity::unlitServerTick);
        }
        return null;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }





}
