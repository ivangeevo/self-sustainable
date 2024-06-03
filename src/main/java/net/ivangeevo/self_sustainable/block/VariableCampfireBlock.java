package net.ivangeevo.self_sustainable.block;

import com.google.common.collect.Maps;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.ivangeevo.self_sustainable.block.interfaces.IVariableCampfireBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.CampfireState;
import net.ivangeevo.self_sustainable.state.property.ModProperties;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.minecraft.SharedConstants;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.*;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Unique;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static net.ivangeevo.self_sustainable.block.entity.CampfireBEManager.isRainingOnCampfire;

public class VariableCampfireBlock extends BlockWithEntity
        implements Waterloggable, IVariableCampfireBlock
{


    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final BooleanProperty SIGNAL_FIRE = Properties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    // Added BTWR Variables
    public static final BooleanProperty HAS_SPIT = ModProperties.HAS_SPIT;
    public static final IntProperty FIRE_LEVEL = IntProperty.of("fire_level", 0, 3);
    public static final EnumProperty<CampfireState> FUEL_STATE = EnumProperty.of("fuel_state", CampfireState.class);

    protected static final VoxelShape SHAPE_WITH_SPIT = VoxelShapes.fullCube();

    /**
     * The shape used to test whether a given block is considered 'smokey'.
     */
    private static final VoxelShape SMOKEY_SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final int field_31049 = 5;
    private final boolean emitsParticles;
    private final int fireDamage;

    public VariableCampfireBlock(boolean emitsParticles, int fireDamage, AbstractBlock.Settings settings)
    {
        super(settings);
        this.emitsParticles = emitsParticles;
        this.fireDamage = fireDamage;
        this.setDefaultState(this.stateManager.getDefaultState().with(HAS_SPIT, false).with(FIRE_LEVEL, 0)
                .with(FUEL_STATE, CampfireState.NORMAL).with(SIGNAL_FIRE, false).with(WATERLOGGED, false)
                .with(FACING, Direction.NORTH));
    }

    private static final VariableCampfireBlock instance =
            new VariableCampfireBlock(getInstance().emitsParticles, getInstance().fireDamage, getInstance().settings);

    public static VariableCampfireBlock getInstance()
    {
        return instance;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack heldStack = player.getStackInHand(hand); // Get the heldStack in the specified hand
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Use this cast to get access to the new variables.
        CampfireBlockEntityAdded campfireAdded;
        campfireAdded = (CampfireBlockEntityAdded) blockEntity;

        if (blockEntity instanceof CampfireBlockEntity campfireBE)
        {
            Optional<CampfireCookingRecipe> optional;

            // Temporary instant light logic.
            /**
             if (player.getStackInHand(hand).isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS) && !state.get(LIT))
             {
             if (!world.isClient())
             { // Only execute on the server
             world.setBlockState(pos, state.with(LIT, true));
             heldStack.damage(1, player, (p) -> { p.sendToolBreakStatus(hand); });
             }
             Ignitable.playLitFX(world, pos);

             return ActionResult.SUCCESS;
             }
             **/

            // Handle stick input
            if (!getHasSpit(world, pos))
            {
                if (heldStack.isOf(Items.STICK))
                {
                    setHasSpit(world, state, pos, true);
                    heldStack.decrement(1); // Decrease the heldStack count

                    return ActionResult.SUCCESS;
                }
            }
            else
            {
                if (!getCookStack(campfireBE).isEmpty() && !heldStack.isIn(ModTags.Items.DIRECTLY_IGNITER_ITEMS))
                {
                    campfireAdded.retrieveItem(world, campfireBE, player);
                    playGetItemSound(world, pos, player);

                    return ActionResult.SUCCESS;
                }

                if (heldStack.isEmpty() && getCookStack(campfireBE).isEmpty())
                {
                    setHasSpit(world, state, pos, false);
                    player.giveItemStack(new ItemStack(Items.STICK));
                    playGetItemSound(world, pos, player);

                    return ActionResult.SUCCESS;
                }
                else if ((optional = campfireBE.getRecipeFor(heldStack)).isPresent())
                {
                    if (getCookStack(campfireBE).isEmpty())
                    {

                        campfireBE.addItem(player,
                                player.getAbilities().creativeMode
                                        ? heldStack.copy()
                                        : heldStack, optional.get().getCookTime());

                        return ActionResult.SUCCESS;
                    }
                }
            }

            if (state.get(FIRE_LEVEL) > 0 || getFuelState(world, pos) == CampfireState.SMOULDERING)
            {
                int itemBurnTime = getItemFuelTime(heldStack);

                if ( heldStack.getItem().getCanBeFedDirectlyIntoCampfire(heldStack) )
                {
                    if ( !world.isClient )
                    {
                        Ignitable.playLitFX(world, pos);

                        campfireAdded.addBurnTime(state, itemBurnTime);
                    }

                    heldStack.decrement(1);

                    return ActionResult.SUCCESS;
                }
            }


        }
        return ActionResult.PASS;
    }

    public static int getItemFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return campfireFuelMap().getOrDefault(item, 0);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, TagKey<Item> tag, int fuelTime) {
        for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(tag)) {
            if (isNonFlammableWood(registryEntry.value())) continue;
            fuelTimes.put(registryEntry.value(), fuelTime);
        }
    }
    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        if (isNonFlammableWood(item2)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + item2.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        fuelTimes.put(item2, fuelTime);
    }

    /**
     * {@return whether the provided {@code item} is in the {@link
     * ItemTags#NON_FLAMMABLE_WOOD non_flammable_wood} tag}
     */
    private static boolean isNonFlammableWood(Item item) {
        return item.getRegistryEntry().isIn(ItemTags.NON_FLAMMABLE_WOOD);
    }

    public static Map<Item, Integer> campfireFuelMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        addFuel(map, Blocks.COAL_BLOCK, 14400);
        addFuel(map, Items.BLAZE_ROD, 12800);

        // Logs
        addFuel(map, Items.BIRCH_LOG, 16000);
        addFuel(map, Items.ACACIA_LOG, 16000);
        addFuel(map, Items.OAK_LOG, 12800);
        addFuel(map, Items.DARK_OAK_LOG, 12800);
        addFuel(map, Items.CHERRY_LOG, 12800);
        addFuel(map, Items.SPRUCE_LOG, 9600);
        addFuel(map, Items.MANGROVE_LOG, 8400);
        addFuel(map, Items.JUNGLE_LOG, 6400);
        addFuel(map, ItemTags.BAMBOO_BLOCKS, 500);

        // Planks
        addFuel(map, Items.BIRCH_PLANKS, 16000);
        addFuel(map, Items.ACACIA_PLANKS, 16000);
        addFuel(map, Items.OAK_PLANKS, 12800);
        addFuel(map, Items.DARK_OAK_PLANKS, 12800);
        addFuel(map, Items.CHERRY_PLANKS, 12800);
        addFuel(map, Items.SPRUCE_PLANKS, 9600);
        addFuel(map, Items.MANGROVE_PLANKS, 8400);
        addFuel(map, Items.JUNGLE_PLANKS, 6400);
        addFuel(map, Items.BAMBOO_PLANKS, 40);

        // Wooden Stairs
        addFuel(map, Items.BIRCH_STAIRS, 400);
        addFuel(map, Items.ACACIA_STAIRS, 400);
        addFuel(map, Items.OAK_STAIRS, 300);
        addFuel(map, Items.DARK_OAK_STAIRS, 300);
        addFuel(map, Items.CHERRY_STAIRS, 300);
        addFuel(map, Items.SPRUCE_STAIRS, 200);
        addFuel(map, Items.MANGROVE_STAIRS, 200);
        addFuel(map, Items.JUNGLE_STAIRS, 70);
        addFuel(map, Items.BAMBOO_STAIRS, 30);


        addFuel(map, Blocks.BAMBOO_MOSAIC, 40);
        addFuel(map, Blocks.BAMBOO_MOSAIC_STAIRS, 30);
        addFuel(map, ItemTags.WOODEN_SLABS, 150);
        addFuel(map, Blocks.BAMBOO_MOSAIC_SLAB, 20);
        addFuel(map, ItemTags.WOODEN_TRAPDOORS, 300);
        addFuel(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        addFuel(map, ItemTags.WOODEN_FENCES, 300);
        addFuel(map, ItemTags.FENCE_GATES, 300);
        addFuel(map, Blocks.NOTE_BLOCK, 300);
        addFuel(map, Blocks.BOOKSHELF, 300);
        addFuel(map, Blocks.CHISELED_BOOKSHELF, 300);
        addFuel(map, Blocks.LECTERN, 300);
        addFuel(map, Blocks.JUKEBOX, 300);
        addFuel(map, Blocks.CHEST, 300);
        addFuel(map, Blocks.TRAPPED_CHEST, 300);
        addFuel(map, Blocks.CRAFTING_TABLE, 300);
        addFuel(map, Blocks.DAYLIGHT_DETECTOR, 300);
        addFuel(map, ItemTags.BANNERS, 300);
        addFuel(map, Items.BOW, 300);
        addFuel(map, Items.FISHING_ROD, 300);
        addFuel(map, Blocks.LADDER, 300);
        addFuel(map, ItemTags.SIGNS, 200);
        addFuel(map, ItemTags.HANGING_SIGNS, 800);
        addFuel(map, Items.WOODEN_SHOVEL, 200);
        addFuel(map, Items.WOODEN_SWORD, 200);
        addFuel(map, Items.WOODEN_HOE, 200);
        addFuel(map, Items.WOODEN_AXE, 200);
        addFuel(map, Items.WOODEN_PICKAXE, 200);
        addFuel(map, ItemTags.WOODEN_DOORS, 200);
        addFuel(map, ItemTags.BOATS, 1200);
        addFuel(map, ItemTags.WOOL, 100);
        addFuel(map, ItemTags.WOODEN_BUTTONS, 100);
        addFuel(map, Items.STICK, 50);
        addFuel(map, ItemTags.SAPLINGS, 100);
        addFuel(map, Items.BOWL, 100);
        addFuel(map, ItemTags.WOOL_CARPETS, 67);
        addFuel(map, Blocks.DRIED_KELP_BLOCK, 4001);
        addFuel(map, Items.CROSSBOW, 300);
        addFuel(map, Blocks.BAMBOO, 50);
        addFuel(map, Blocks.DEAD_BUSH, 100);
        addFuel(map, Blocks.SCAFFOLDING, 50);
        addFuel(map, Blocks.LOOM, 300);
        addFuel(map, Blocks.BARREL, 300);
        addFuel(map, Blocks.CARTOGRAPHY_TABLE, 300);
        addFuel(map, Blocks.FLETCHING_TABLE, 300);
        addFuel(map, Blocks.SMITHING_TABLE, 300);
        addFuel(map, Blocks.COMPOSTER, 300);
        addFuel(map, Blocks.AZALEA, 100);
        addFuel(map, Blocks.FLOWERING_AZALEA, 100);
        addFuel(map, Blocks.MANGROVE_ROOTS, 300);
        return map;
    }

    private CampfireState getFuelState(WorldAccess blockAccess, BlockPos pos)
    {
        return getFuelState(blockAccess.getBlockState(pos));
    }




    private static ItemStack getCookStack(CampfireBlockEntity campfireBE)
    {
        return campfireBE.getItemsBeingCooked().get(0);
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.get(FIRE_LEVEL) >= 2 && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            entity.damage(world.getDamageSources().inFire(), this.fireDamage);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CampfireBlockEntity) {
            ItemScatterer.spawn(world, pos, ((CampfireBlockEntity)blockEntity).getItemsBeingCooked());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos;
        World worldAccess = ctx.getWorld();
        boolean bl = worldAccess.getFluidState(blockPos = ctx.getBlockPos()).getFluid() == Fluids.WATER;
        return this.getDefaultState().with(WATERLOGGED, bl).with(SIGNAL_FIRE, this.isSignalFireBaseBlock(worldAccess.getBlockState(blockPos.down()))).with(FIRE_LEVEL,0).with(FACING, ctx.getHorizontalPlayerFacing());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED).booleanValue()) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        if (direction == Direction.DOWN) {
            return state.with(SIGNAL_FIRE, this.isSignalFireBaseBlock(neighborState));
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean isSignalFireBaseBlock(BlockState state) {
        return state.isOf(Blocks.HAY_BLOCK);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(FIRE_LEVEL) < 1) {
            return;
        }
        if (random.nextInt(10) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_CAMPFIRE_CRACKLE, SoundCategory.BLOCKS, 0.5f + random.nextFloat(), random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.emitsParticles && random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle(ParticleTypes.LAVA, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, random.nextFloat() / 2.0f, 5.0E-5, random.nextFloat() / 2.0f);
            }
        }
    }

    public static void extinguish(@Nullable Entity entity, WorldAccess world, BlockPos pos, BlockState state) {
        BlockEntity blockEntity;
        if (world.isClient()) {
            for (int i = 0; i < 20; ++i) {
                CampfireBlock.spawnSmokeParticle((World)world, pos, state.get(SIGNAL_FIRE), true);
            }
        }
        if ((blockEntity = world.getBlockEntity(pos)) instanceof CampfireBlockEntity) {
            ((CampfireBlockEntity)blockEntity).spawnItemsBeingCooked();
        }
        world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.get(Properties.WATERLOGGED).booleanValue() && fluidState.getFluid() == Fluids.WATER) {
            boolean bl = state.get(FIRE_LEVEL) >= 1;
            if (bl) {
                if (!world.isClient()) {
                    world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                CampfireBlock.extinguish(null, world, pos, state);
            }
            world.setBlockState(pos, state.with(WATERLOGGED, true).with(FIRE_LEVEL, 0), Block.NOTIFY_ALL);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        BlockPos blockPos = hit.getBlockPos();
        if (!world.isClient && projectile.isOnFire() && projectile.canModifyAt(world, blockPos) && state.get(FIRE_LEVEL) == 0 && !state.get(WATERLOGGED)) {
            world.setBlockState(blockPos, (BlockState)state.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    public static void spawnSmokeParticle(World world, BlockPos pos, boolean isSignal, boolean lotsOfSmoke) {
        Random random = world.getRandom();
        DefaultParticleType defaultParticleType = isSignal ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        world.addImportantParticle(defaultParticleType, true, (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (lotsOfSmoke) {
            world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static boolean isLitCampfireInRange(World world, BlockPos pos) {
        for (int i = 1; i <= 5; ++i) {
            BlockPos blockPos = pos.down(i);
            BlockState blockState = world.getBlockState(blockPos);
            if (CampfireBlock.isLitCampfire(blockState)) {
                return true;
            }
            boolean bl = VoxelShapes.matchesAnywhere(SMOKEY_SHAPE, blockState.getCollisionShape(world, pos, ShapeContext.absent()), BooleanBiFunction.AND);
            if (!bl) continue;
            BlockState blockState2 = world.getBlockState(blockPos.down());
            return CampfireBlock.isLitCampfire(blockState2);
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState state) {
        return state.contains(FIRE_LEVEL) && state.isIn(BlockTags.CAMPFIRES) && state.get(FIRE_LEVEL) != 0;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.get(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_SPIT, FIRE_LEVEL, FUEL_STATE, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (world.isClient)
        {
            if (state.get(FIRE_LEVEL) > 1)
            {
                return CampfireBlock.checkType(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::clientTick);
            }
        }
        else
        {
            if (state.get(FIRE_LEVEL) > 1)
            {
                return CampfireBlock.checkType(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::litServerTick);
            }
            return CampfireBlock.checkType(type, BlockEntityType.CAMPFIRE, CampfireBlockEntity::unlitServerTick);
        }
        return null;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public static boolean canBeLit(BlockState state) {
        return state.isIn(BlockTags.CAMPFIRES, statex -> statex.contains(WATERLOGGED) && statex.contains(FIRE_LEVEL)) && !state.get(WATERLOGGED) && state.get(FIRE_LEVEL) == 0;
    }

    public static boolean getHasSpit(WorldAccess blockAccess, BlockPos pos)
    {
        return blockAccess.getBlockState(pos).get(HAS_SPIT);
    }


    public static boolean setHasSpit(World world, BlockState state, BlockPos pos, boolean bHasSpit)
    {
        return !world.isClient() && world.setBlockState(pos, state.with(HAS_SPIT, bHasSpit));
    }

    private static void playGetItemSound(World world, BlockPos pos, PlayerEntity player)
    {
        world.playSound(null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                ( ( player.getRandom().nextFloat() - player.getRandom().nextFloat() ) * 0.7F + 1F ) * 2F);
    }

    @Override
    public boolean getCanBeSetOnFireDirectlyByItem(WorldAccess blockAccess, BlockPos pos) {
        return true;
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return blockAccess.getBlockState(pos).get(FIRE_LEVEL) == 0 && getFuelState(blockAccess.getBlockState(pos)) == CampfireState.NORMAL;
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos)
    {
        if (this.getCanBeSetOnFireDirectly(world, pos))
        {

            if (!isRainingOnCampfire(world, pos))
            {
                changeFireLevel(world, pos, world.getBlockState(pos), 1);

                CampfireBlockEntity campfireBE = (CampfireBlockEntity) world.getBlockEntity(pos);

                ((CampfireBlockEntityAdded)campfireBE).onFirstLit();

                BlockPos soundPos =
                        new BlockPos(
                                (int) (pos.getX() + 0.5D),
                                (int) (pos.getY() + 0.5D),
                                (int) (pos.getZ() + 0.5D));

                world.playSound(null, soundPos,
                        SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.BLOCKS, 1F,
                        world.random.nextFloat() * 0.4F + 0.8F);

                //TODO?: Add portal creation logic with campfire.
                /**
                 if (!Block.portal.tryToCreatePortal(world, i, j, k)) {
                 // FCTODO: A bit hacky here.  Should probably be a general way to start a
                 // bigger fire atop flammable blocks

                 int iBlockBelowID = world.getBlockId(i, j - 1, k);

                 if (iBlockBelowID == Block.netherrack.blockID || iBlockBelowID == BTWBlocks.fallingNetherrack.blockID) {
                 world.setBlockWithNotify(i, j, k, Block.fire.blockID);
                 }
                 }
                 **/
            }
            else
            {
                Ignitable.playExtinguishSound(world, pos, false);
            }

            return true;
        }

        return false;
    }

    public void changeFireLevel(World world, BlockPos pos, BlockState state, int newFireLevel)
    {
        //CampfireBlock.campfireChangingState = true;

        world.setBlockState( pos, state.with(FIRE_LEVEL, newFireLevel), Block.NOTIFY_ALL);

        //CampfireBlock.campfireChangingState = false;
    }
    public CampfireState getFuelState(BlockState state) {
        return state.get(FUEL_STATE);
    }

    public void relightFire(BlockState state) {

    }

    public int getFireLevel(BlockState state) {
        return state.get(FIRE_LEVEL);
    }

    public BlockState setFireLevel (BlockState state, int newLevel)
    {
        return state.with(FIRE_LEVEL, newLevel);
    }

    public void extinguishFire(World world, BlockState state, BlockPos pos, boolean bSmoulder)
    {

        if ( bSmoulder )
        {
            setFuelState(world, pos, CampfireState.SMOULDERING.ordinal());
        }
        else
        {
            setFuelState(world, pos, CampfireState.BURNED_OUT.ordinal());
        }

        changeFireLevel(world, pos, state, 0);

        if ( !world.isClient() )
        {
            Ignitable.playExtinguishSound(world, pos, true);
        }
    }

    public void stopSmouldering(World world, BlockPos pos)
    {
        setFuelState(world, pos, CampfireState.SMOULDERING.ordinal());
    }

    public BlockState setFuelState(World world, BlockPos pos, int fireState)
    {
        BlockState tempState = world.getBlockState(pos);
        return tempState.with(FUEL_STATE, CampfireState.convertToEnumState(fireState));
    }

    public void relightFire(World world, BlockPos pos, BlockState state)
    {
        changeFireLevel(world, pos, state, 1);
    }


    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify)
    {
        Optional<NetherPortal> optional;
        if (oldState.isOf(state.getBlock()))
        {
            return;
        }
        /**
         if (isOverworldOrNether(world)
         && (optional = NetherPortal.getNewPortal(world, pos, Direction.Axis.X)).isPresent() && state.get(LIT))
         {
         optional.get().createPortal();
         return;
         }
         **/
        if (!state.canPlaceAt(world, pos))
        {
            world.removeBlock(pos, false);
        }

    }

    @Unique
    private static boolean isOverworldOrNether(World world)
    {
        return world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.NETHER;
    }



}
