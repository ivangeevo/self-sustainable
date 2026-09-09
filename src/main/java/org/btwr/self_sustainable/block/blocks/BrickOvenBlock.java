package org.btwr.self_sustainable.block.blocks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.*;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.entity.BrickOvenBE;
import org.btwr.self_sustainable.block.interfaces.IgnitableBlock;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import org.btwr.self_sustainable.sound.ModSoundEvents;
import org.btwr.self_sustainable.state.property.ModProperties;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.self_sustainable.util.TickableBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BrickOvenBlock extends BlockWithEntity implements IgnitableBlock {

    public static final MapCodec<BrickOvenBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    createSettingsCodec(),
                    Codec.BOOL.fieldOf("is_mortared").forGetter(BrickOvenBlock::isMortared)
            ).apply(instance, BrickOvenBlock::new)
    );
    @Override protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty FUEL_LEVEL = ModProperties.FUEL_LEVEL;

    public static final float CLICK_Y_TOP_PORTION = 6f / 16f;
    public static final float CLICK_Y_BOTTOM_PORTION = 6f / 16f;

    private final boolean isMortared;

    public boolean isMortared() {
        return isMortared;
    }

    public BrickOvenBlock(Settings settings, boolean isMortared) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH).with(LIT, false));
        this.isMortared = isMortared;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, FUEL_LEVEL);
    }

    @Override
    public ItemActionResult onUseWithItem(
            ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit
    ) {
        TagKey<Item> mortaringItems = null;

        boolean toughEnvLoaded = false;

        if (FabricLoader.getInstance().isModLoaded("tough_environment")) {
            toughEnvLoaded = true;
            mortaringItems = TagKey.of(RegistryKeys.ITEM, Identifier.of("tough_environment", "mortaring_items"));
        }

        boolean canTEMortar = toughEnvLoaded && (mortaringItems != null && stack.isIn(mortaringItems));

        if (canTEMortar || (stack.isOf(Items.CLAY_BALL) && !isMortared())) {
            BlockState mortaredState = ModBlocks.OVEN_BRICK_MORTARED.getDefaultState()
                    .with(LIT, state.get(LIT))
                    .with(FUEL_LEVEL, state.get(FUEL_LEVEL))
                    .with(FACING, state.get(FACING));

            if (!world.isClient) {
                BlockEntity oldBE = world.getBlockEntity(pos);
                NbtCompound nbt = oldBE != null ? oldBE.createNbtWithId(world.getRegistryManager()) : null;

                world.setBlockState(pos, mortaredState);

                if (nbt != null && world.getBlockEntity(pos) instanceof BrickOvenBE newOvenBE) {
                    nbt.putString("id", Objects.requireNonNull(BlockEntityType.getId(newOvenBE.getType())).toString());
                    newOvenBE.read(nbt, world.getRegistryManager());
                    newOvenBE.markDirty();
                }

                world.playSound(null, pos, ModSoundEvents.OVEN_MORTAR, SoundCategory.BLOCKS);
            }
        }

        if (hit.getSide() != state.get(FACING)) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            Block b = blockItem.getBlock();

            if (stack.getItem() instanceof VerticallyAttachableBlockItem) {
                boolean isWallAttachable = b instanceof WallTorchBlock
                        || b instanceof UnlitWallTorchBlock
                        || b instanceof CrudeWallTorchBlock
                        || b instanceof WallMountedBlock
                        || b instanceof AbstractBannerBlock
                        || b instanceof WallSignBlock
                        || b instanceof WallHangingSignBlock;

                if (isWallAttachable) {
                    // Block placement, but still let onUse() fire
                    // so bottom-portion igniter logic still works
                    return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
                }
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        // Only accessible from the front face
        if (state.get(FACING) != hit.getSide()) {
            return ActionResult.FAIL;
        }

        BrickOvenBE be = getBlockEntity(world, pos);
        if (be == null) return ActionResult.PASS;

        ItemStack heldStack = player.getMainHandStack();
        ItemStack cookStack = be.getCookStack();

        if (isTopPortionClick(hit, pos)) {
            // --- Cook slot interaction ---
            if (!cookStack.isEmpty()) {
                // Trigger achievement equivalent
                // AchievementEventDispatcher.triggerEvent(AchievementEvents.ItemEvent.class, player, cookStack);
                be.givePlayerCookStack(world, player, hit.getSide());
                return ActionResult.SUCCESS;
            } else {
                if (!heldStack.isEmpty() && isValidCookItem(world, heldStack)) {
                    if (!world.isClient) {
                        be.addCookStack(heldStack.copyWithCount(1));
                    }
                    if (!player.getAbilities().creativeMode) {
                        heldStack.decrement(1);
                    }
                    return ActionResult.SUCCESS;
                }
            }

            if (heldStack.isIn(ModTags.Items.DIRECT_IGNITERS)) {
                return ActionResult.CONSUME_PARTIAL;
            }

        } else if (isBottomPortionClick(hit, pos) && !heldStack.isEmpty()) {
            // Handle fuel here
            Item item = heldStack.getItem();

            if (heldStack.isIn(ModTags.Items.DIRECT_IGNITERS)) {
                if (!state.get(LIT)) {
                    state.getBlock().btwr$setOnFireDirectly(world, pos);
                    return ActionResult.SUCCESS;
                }
                return ActionResult.CONSUME_PARTIAL;
            }

            if (item.btwr$getCanBeFedDirectlyIntoBrickOven(heldStack)) {
                if (!world.isClient) {
                    int consumed = be.attemptToAddFuel(heldStack);

                    if (consumed > 0) {
                        if (state.get(LIT)) {
                            world.playSound(
                                    null,
                                    pos,
                                    ModSoundEvents.OVEN_INSERT_FUEL_ACTIVE,
                                    SoundCategory.BLOCKS,
                                    0.2f + world.random.nextFloat() * 0.1f,
                                    world.random.nextFloat() * 0.25f + 1.25f
                            );
                        } else {
                            world.playSound(
                                    null,
                                    pos,
                                    ModSoundEvents.OVEN_INSERT_FUEL,
                                    SoundCategory.BLOCKS,
                                    0.25f,
                                    ((world.random.nextFloat() - world.random.nextFloat()) * 0.7f + 1.0f) * 2.0f
                            );
                        }

                        if (!player.getAbilities().creativeMode) {
                            heldStack.decrement(consumed);
                        }
                    }
                }

                return ActionResult.SUCCESS;
            }

            return ActionResult.FAIL;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);

        if (isMortared()) return;

        if (btwr$hasNeighborWithMortarInContact(world, pos)) {
            world.addSyncedBlockEvent(pos, this, 0, 0);
            world.scheduleBlockTick(pos, this, 40);
        } else {
            world.scheduleBlockTick(pos, this, 10);
        }
    }

    @Override
    public boolean btwr$onMortarApplied(World world, BlockPos pos) {
        if (world.isClient) {
            return !isMortared();
        }

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof BrickOvenBE ovenBE && !isMortared()) {
            return ovenBE.applyMortar();
        }
        return false;
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if (!isMortared()) {
            // Loose oven requires solid surface below
            BlockState below = world.getBlockState(pos.down());

            if (!below.isSideSolidFullSquare(world, pos.down(), Direction.UP)) {
                return false;
            }
        }
        return super.canPlaceAt(state, world, pos);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (!isMortared() && !world.isClient) {
            BlockState below = world.getBlockState(pos.down());
            if (!below.isSideSolidFullSquare(world, pos.down(), Direction.UP)) {
                dropStacks(state, world, pos);
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    public boolean btwr$hasLargeCenterHardPointToFacing(WorldAccess world, BlockPos pos, Direction facing, boolean ignoreTransparency) {
        Direction blockFacing = world.getBlockState(pos).get(FACING);
        return blockFacing != facing;
    }

    public void updateOvenBlockState(boolean burning, ServerWorld world, BlockPos pos) {
        BlockState current = world.getBlockState(pos);

        if (!(world.getBlockEntity(pos) instanceof BrickOvenBE oven)) return;

        boolean mortared = oven.mortarOnNextUpdate || current.getBlock() instanceof BrickOvenBlock b && b.isMortared();

        Block targetBlock = mortared ? ModBlocks.OVEN_BRICK_MORTARED : ModBlocks.OVEN_BRICK;

        BlockState newState = targetBlock.getDefaultState()
                .with(BrickOvenBlock.FACING, current.get(BrickOvenBlock.FACING))
                .with(BrickOvenBlock.FUEL_LEVEL, current.get(FUEL_LEVEL))
                .with(BrickOvenBlock.LIT, burning);

        if (current.getBlock() != targetBlock) {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);

            if (world.getBlockEntity(pos) instanceof BrickOvenBE newOven) {
                newOven.cancelRemoval();
                if (oven.mortarOnNextUpdate) {
                    oven.mortarOnNextUpdate = false;
                }
            }
        } else {
            world.setBlockState(pos, newState, Block.NOTIFY_ALL);
        }
    }

    @Override
    public boolean btwr$getCanBlockLightItemOnFire(WorldAccess world, BlockPos pos) {
        return world.getBlockState(pos).get(LIT);
    }

    @Override
    public boolean btwr$getCanBeSetOnFireDirectly(WorldAccess world, BlockPos pos) {
        if (!world.getBlockState(pos).get(LIT)) {
            BrickOvenBE be = getBlockEntity(world, pos);

            // uses the visual fuel level rather than the actual fuel level, so this will work on the client
            return be != null && be.getVisualFuelLevel() > 0;
        }

        return false;
    }


    @Override
    public boolean btwr$setOnFireDirectly(World world, BlockPos pos) {
        if (!world.getBlockState(pos).get(LIT)) {
            BrickOvenBE be = getBlockEntity(world, pos);
            if (be != null && be.attemptToLight()) {
                world.playSound(
                        null,
                        BlockPos.ofFloored(pos.toCenterPos()),
                        ModSoundEvents.OVEN_IGNITE,
                        SoundCategory.BLOCKS,
                        1F,
                        world.random.nextFloat() * 0.4F + 0.8F
                );

                return true;
            }
        }

        return false;
    }

    @Override
    public int btwr$getChanceOfFireSpreadingDirectlyTo(WorldAccess world, BlockPos pos) {
        if (world.getBlockState(pos).get(LIT)) return 0;

        BrickOvenBE be = getBlockEntity(world, pos);
        if (be != null && be.hasValidFuel()) {
            return 60;
        }
        return 0;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof BrickOvenBE ovenBE) {
            // Drops the contents inside when the block is destroyed
            if (!newState.isOf(ModBlocks.OVEN_BRICK_MORTARED)) {
                ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), ovenBE.getCookStack());
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!state.get(LIT)) return;

        double d = (double) pos.getX() + 0.5;
        double e = pos.getY();
        double f = (double) pos.getZ() + 0.5;

        if (world.getRandom().nextDouble() < 0.05) {
            world.playSound(
                    d, e, f,
                    SoundEvents.BLOCK_FIRE_AMBIENT,
                    SoundCategory.BLOCKS,
                    0.25F + world.random.nextFloat() * 0.25F,
                    0.5F + world.random.nextFloat() * 0.25F, false
            );

            Direction direction = state.get(BrickOvenBlock.FACING);
            Direction.Axis axis = direction.getAxis();

            double g = 0.52;
            double h = world.getRandom().nextDouble() * 0.6 - 0.3;
            double i = axis == Direction.Axis.X ? (double) direction.getOffsetX() * 0.52 : h;
            double j = world.getRandom().nextDouble() * 6.0 / 16.0;
            double k = axis == Direction.Axis.Z ? (double) direction.getOffsetZ() * 0.52 : h;

            world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
            world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0);
        }

            BrickOvenBE ovenBE = (BrickOvenBE) world.getBlockEntity(pos);
            assert ovenBE != null;
            int iFuelLevel = ovenBE.getVisualFuelLevel();

            if (iFuelLevel == 1) {
                Direction facing = world.getBlockState(pos).get(FACING);

                float fX = (float)facing.getId() + 0.5F;
                float fY = (float)facing.getId() + 0.0F + random.nextFloat() * 6.0F / 16.0F;
                float fZ = (float)facing.getId() + 0.5F;

                float fFacingOffset = 0.52F;
                float fRandOffset = random.nextFloat() * 0.6F - 0.3F;

                if (facing == Direction.WEST) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, fX - fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D);
                }
                else if (facing == Direction.EAST) {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fFacingOffset, fY, fZ + fRandOffset, 0.0D, 0.0D, 0.0D );
                }
                else if (facing == Direction.NORTH) {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fRandOffset, fY, fZ - fFacingOffset, 0.0D, 0.0D, 0.0D );
                }
                else if (facing == Direction.SOUTH) {
                    world.addParticle( ParticleTypes.LARGE_SMOKE, fX + fRandOffset, fY, fZ + fFacingOffset, 0.0D, 0.0D, 0.0D );
                }
            }

            ItemStack cookStack = ovenBE.getCookStack();

            if (cookStack != null && ovenBE.getRecipeFor(cookStack).isPresent()) {
                for (int iTempCount = 0; iTempCount < 1; ++iTempCount) {
                    float fX = pos.getX() + 0.375F + random.nextFloat() * 0.25F;
                    float fY = pos.getY() + 0.45F + random.nextFloat() * 0.1F;
                    float fZ = pos.getZ() + 0.375F + random.nextFloat() * 0.25F;

                    world.addParticle( ParticleTypes.CLOUD, fX, fY, fZ, 0D, 0D, 0D );
                }
            }
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BrickOvenBE be = getBlockEntity(world, pos);
        return be != null ? BrickOvenBE.calcRedstoneFromOven(be) : 0;
    }

    @Override
    protected boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !isMortared();
    }

    @Nullable
    private static BrickOvenBE getBlockEntity(BlockView world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof BrickOvenBE o ? o : null;
    }

    public boolean isValidCookItem(World world, ItemStack stack) {
        SingleStackRecipeInput singleStackRecipeInput = new SingleStackRecipeInput(stack);
        return world.getRecipeManager()
                .getFirstMatch(OvenCookingRecipe.Type.INSTANCE, singleStackRecipeInput, world)
                .isPresent();
    }

    @Override
    public boolean btwr$hasMortar(WorldAccess world, BlockPos pos) {
        return isMortared();
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos pos = hit.getBlockPos();

        if (!canProjectileLightUp(hit, pos, state, world)) return;
        
        if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, pos)) {
            world.setBlockState(pos, state.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            world.playSound(
                    null,
                    BlockPos.ofFloored(pos.toCenterPos()),
                    ModSoundEvents.OVEN_IGNITE, SoundCategory.BLOCKS,
                    0.2F + world.random.nextFloat() * 0.1F,
                    world.random.nextFloat() * 0.25F + 1.25F
            );
        }
    }

    @Override @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BrickOvenBE(pos, state);
    }


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return TickableBlockEntity.getTicker();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public boolean canProjectileLightUp(BlockHitResult hit, BlockPos pos, BlockState state, World world) {
        if (state.get(LIT)) return false;
        if (hit.getSide() != state.get(FACING)) return false;

        if (!isBottomPortionClick(hit, pos)) return false;

        BrickOvenBE be = getBlockEntity(world, pos);
        return be != null && be.hasValidFuel();
    }

    public boolean isBottomPortionClick(BlockHitResult hit, BlockPos pos) {
        double relativeClickY = hit.getPos().getY() - pos.getY();
        return relativeClickY < CLICK_Y_BOTTOM_PORTION;
    }

    public boolean isTopPortionClick(BlockHitResult hit, BlockPos pos) {
        double relativeClickY = hit.getPos().getY() - pos.getY();
        return relativeClickY > CLICK_Y_TOP_PORTION;
    }

}