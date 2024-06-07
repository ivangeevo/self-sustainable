package net.ivangeevo.self_sustainable.block.blocks;

import net.ivangeevo.self_sustainable.block.entity.BrickOvenBE;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

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

    @Override @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBE( pos, state );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(LIT, FACING, FUEL_LEVEL);
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos)
    {

        if ( !world.getBlockState(pos).get(LIT) && world.getBlockState(pos).get(FUEL_LEVEL) > 0)
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
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos)
    {
        if ( !blockAccess.getBlockState(pos).get(LIT) )
        {
            BrickOvenBE ovenBE = (BrickOvenBE) blockAccess.getBlockEntity( pos );

            // uses the visual fuel level rather than the actualy fuel level so this will work on the client

            assert ovenBE != null;

            return ovenBE.getVisualFuelLevel() > 0;
        }

        return false;
    }
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getStackInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        Item item = heldStack.getItem();

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

                if (!ovenBE.getCookStack().isEmpty())
                {
                    ovenBE.retrieveItem(world, player);

                    world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS);
                    return ActionResult.SUCCESS;
                }
                else if ( !heldStack.isEmpty() && (optional = ovenBE.getRecipeFor(heldStack)).isPresent() )
                {
                    if ( !world.isClient() && ovenBE.getCookStack().isEmpty() && ovenBE.addItem(player,
                            player.getAbilities().creativeMode ? heldStack.copy() : heldStack, optional.get().getCookTime()))
                    {

                        return ActionResult.SUCCESS;
                    }
                }
            }
            else if (relativeClickY < clickYBottomPortion && !heldStack.isEmpty())
            {

                if ( item.getCanBeFedDirectlyIntoBrickOven(heldStack) )
                {

                    if (!world.isClient) {
                        int iItemsConsumed = ovenBE.attemptToAddFuel(heldStack);

                        if (iItemsConsumed > 0) {
                            if (state.get(LIT)) {
                                Ignitable.playLitFX(world, pos);
                            } else {
                                this.playPopSound(world, pos);
                            }

                            heldStack.setCount(heldStack.getCount() - iItemsConsumed);
                        }
                    }

                    return ActionResult.SUCCESS;
                }




                // keep just in case. for now...
                /**
                // Try to ignite
                if (heldStack.getItem() instanceof FlintAndSteelItem || player.getStackInHand(hand).isIn(ModTags.Items.DIRECT_IGNITERS)) {
                    /**
                    if (state.get(FUEL_LEVEL) > 0) {
                        if (!state.get(LIT)) {
                            world.setBlockState(pos, state.with(LIT, true));
                            Ignitable.playLitFX(world, pos);
                            heldStack.damage(1, player,
                                    (p) -> p.sendToolBreakStatus(player.getActiveHand()));
                        }

                        return ActionResult.SUCCESS;
                    } else {
                        Ignitable.playExtinguishSound(world, pos, false);
                    }

                }
                // try to add fuel
                else
                {
                    if (!world.isClient)
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
                }
                **/

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
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ovenBE.getCookStack());
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {

        if ( state.get(LIT) )
        {
            BrickOvenBE ovenBE = (BrickOvenBE) world.getBlockEntity( pos );
            int iFuelLevel = ovenBE.getVisualFuelLevel();

            if ( iFuelLevel == 1 )
            {
                Direction iFacing = world.getBlockState( pos ).get(FACING);

                float fX = (float)iFacing.getId() + 0.5F;
                float fY = (float)iFacing.getId() + 0.0F + random.nextFloat() * 6.0F / 16.0F;
                float fZ = (float)iFacing.getId() + 0.5F;

                float fFacingOffset = 0.52F;
                float fRandOffset = random.nextFloat() * 0.6F - 0.3F;

                if ( iFacing.getId() == 4 )
                {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, fX - fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D );
                }
                else if ( iFacing.getId() == 5 )
                {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D );
                }
                else if ( iFacing.getId() == 2 )
                {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fRandOffset, fY, fZ - fFacingOffset, 0.0D, 0.0D, 0.0D );
                }
                else if ( iFacing.getId() == 3 )
                {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fRandOffset, fY, fZ + fFacingOffset, 0.0D, 0.0D, 0.0D );
                }
            }

            ItemStack cookStack = ovenBE.getCookStack();

            if ( cookStack != null && ovenBE.getRecipeFor(cookStack).isPresent() )
            {
                for ( int iTempCount = 0; iTempCount < 1; ++iTempCount )
                {
                    float fX = pos.getX() + 0.375F + random.nextFloat() * 0.25F;
                    float fY = pos.getY() + 0.45F + random.nextFloat() * 0.1F;
                    float fZ = pos.getZ() + 0.375F + random.nextFloat() * 0.25F;

                    world.addParticle( ParticleTypes.CLOUD, fX, fY, fZ, 0D, 0D, 0D );
                }
            }
        }

        super.randomDisplayTick( state, world, pos, random );

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
        return state.get(FUEL_LEVEL) > 0;
    }


}
