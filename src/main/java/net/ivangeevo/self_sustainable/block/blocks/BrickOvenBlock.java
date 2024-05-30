package net.ivangeevo.self_sustainable.block.blocks;

import com.google.common.collect.Maps;
import net.ivangeevo.self_sustainable.block.ModBlocks;
import net.ivangeevo.self_sustainable.block.entity.BrickOvenBE;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.SharedConstants;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BrickOvenBlock extends BlockWithEntity implements Ignitable
{
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty FUEL_LEVEL = ModProperties.FUEL_LEVEL;
    protected final float clickYTopPortion = (6F / 16F);
    protected final float clickYBottomPortion = (6F / 16F);

    public BrickOvenBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(LIT,false)
                .with(FUEL_LEVEL, 0)
                .with(FACING, Direction.NORTH));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBE( pos, state );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, FUEL_LEVEL);
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos) {

        if (world.getBlockState(pos) == this.getDefaultState().with(LIT, true))
        {
            if (world.getBlockEntity(pos) instanceof BrickOvenBE ovenBE)
            {
                if ( ovenBE.attemptToLight() )
                {
                    BlockPos soundPos = new BlockPos((int) (pos.getX() + 0.5D), (int) (pos.getY() + 0.5D), (int) (pos.getZ() + 0.5D));
                    world.playSound(null, soundPos , SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS ,1F, world.random.nextFloat() * 0.4F + 0.8F);
                    return true;
                }

            }


        }

        return false;

    }

    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        double relativeClickY = hit.getPos().getY() - pos.getY();

        if (hit.getSide() != state.get(FACING))
        {
            return ActionResult.FAIL;
        }


        if (blockEntity instanceof BrickOvenBE ovenBE)
        {
            Optional<OvenCookingRecipe> optional;

            if (relativeClickY > clickYTopPortion)
            {

                if (!ovenBE.getStack().isEmpty())
                {


                    ovenBE.retrieveItem(world, player);

                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);
                    return ActionResult.SUCCESS;
                }
                else if ( !heldStack.isEmpty() && (optional = ovenBE.getRecipeFor(heldStack)).isPresent() )
                {
                    if ( !world.isClient() && ovenBE.getStack().isEmpty() && ovenBE.addItem(player,
                            player.getAbilities().creativeMode ? heldStack.copy() : heldStack, optional.get().getCookTime()))
                    {

                        return ActionResult.SUCCESS;
                    }
                }

                return ActionResult.SUCCESS;
            }
            else if (relativeClickY < clickYBottomPortion && !heldStack.isEmpty())
            {
                // Try to ignite
                if ( heldStack.getItem() instanceof FlintAndSteelItem || player.getStackInHand(hand).isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS) )
                {
                    if (state.get(FUEL_LEVEL) > 0 && !state.get(LIT))
                    {
                        world.setBlockState(pos, state.with(LIT, true));
                        Ignitable.playLitFX(world, pos);
                        heldStack.damage(1, player, (p) -> p.sendToolBreakStatus(player.getActiveHand()));
                        return ActionResult.SUCCESS;
                    }
                }
                // try to add fuel
                else
                {

                    // Use the attemptToAddFuel method to try and add fuel
                    int numItemsConsumed = ovenBE.attemptToAddFuel(heldStack);

                    if (numItemsConsumed > 0) {
                        if (state.get(LIT)) {
                            Ignitable.playLitFX(world, pos);
                        } else {
                            this.playPopSound(world, pos);
                        }

                        heldStack.split(numItemsConsumed);


                    }
                }

                return ActionResult.SUCCESS;
            }

        }

        return ActionResult.PASS;
    }

    private void playPopSound(World world, BlockPos pos) {
        BlockPos soundPos = new BlockPos(
                (int) ((double) pos.getX() + 0.5D),
                (int) ((double) pos.getY() + 0.5D),
                (int) ((double) pos.getZ() + 0.5D));

        world.playSound(null, soundPos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS,
                0.25F, (world.random.nextFloat() - world.random.nextFloat()) * 0.7F + 1.0F);

    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        if (world.isClient)
        {
            return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBE::clientTick);
        }
        else
        {
            return BrickOvenBlock.checkType(type, ModBlockEntities.OVEN_BRICK, BrickOvenBE::serverTick);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx)
    {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {

        if (state.isOf(newState.getBlock()))
        {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof BrickOvenBE ovenBE)
        {
            // Drops the contents inside when the block is destroyed
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ovenBE.getStack());
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) {
            return;
        }

        if (random.nextInt(10) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5,
                    SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(),
                    random.nextFloat() * 0.7f + 0.6f, false);
        }

    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile)
    {
        BlockPos blockPos = hit.getBlockPos();

        double relativeClickY = hit.getPos().getY() - blockPos.getY();

        // Allow projectile interaction only from the facing side
        if (hit.getSide() == state.get(FACING) && canLightUp(state) && relativeClickY < clickYBottomPortion)
        {
            if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && !state.get(LIT))
            {
                world.setBlockState(blockPos, state.with(Properties.LIT, true),
                        Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

                Ignitable.playLitFX(world, blockPos);

            }


        }
    }

    public boolean canLightUp(BlockState state)
    {
        return  state.get(FUEL_LEVEL) > 0;
    }


}
