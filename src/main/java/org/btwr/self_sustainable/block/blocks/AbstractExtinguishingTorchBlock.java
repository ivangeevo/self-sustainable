package org.btwr.self_sustainable.block.blocks;

import org.btwr.self_sustainable.block.ModBlocks;
import org.btwr.self_sustainable.block.entity.TorchBE;
import org.btwr.self_sustainable.block.entity.util.FuelBurningBlock;
import org.btwr.self_sustainable.block.interfaces.Ignitable;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.component.ModComponentsTypes;
import org.btwr.self_sustainable.item.component.TorchFuelComponent;
import org.btwr.self_sustainable.item.items.CrudeTorchBlockItem;
import org.btwr.self_sustainable.tag.ModTags;
import org.btwr.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractExtinguishingTorchBlock extends BlockWithEntity implements BlockEntityProvider, FuelBurningBlock {

    public ParticleEffect particle;
    public TorchFireState fireState;
    public ModTorchHandler handler;
    protected static final VoxelShape SHAPE = Block.createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);

    public AbstractExtinguishingTorchBlock(AbstractBlock.Settings settings, ParticleEffect particle, TorchFireState fireLevel) {
        super(settings);
        this.particle = particle;
        this.fireState = fireLevel;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TorchBE(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
       return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (fireState == TorchFireState.LIT || fireState == TorchFireState.SMOULDER) {
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
        }

        if (fireState == TorchFireState.LIT) {
            displayParticle(this.particle, state, world, pos);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        Hand hand = player.getActiveHand();
        ItemStack stack = player.getStackInHand(hand);

        // Try extinguishing the torch
        if (fireState == TorchFireState.LIT || fireState == TorchFireState.SMOULDER) {
            if (tryUse(ModTags.Items.TORCH_EXTINGUISHERS, stack)) {
                this.extinguish(world, pos, state);
                player.swingHand(hand);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack)
    {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof TorchBE be) {
            if (itemStack.getItem() instanceof CrudeTorchBlockItem) {
                TorchFuelComponent fuelComponent = be.getComponents().get(ModComponentsTypes.TORCH_FUEL);
                if (fuelComponent == null) return;
                int fuel = fuelComponent.getFuel();
                fuelComponent.setFuel(fuel);
            }
        }
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return AbstractTorchBlock.sideCoversSmallSquare(world, pos.down(), Direction.UP);
    }

    @Override
    public boolean getCanBeSetOnFireDirectly(WorldAccess blockAccess, BlockPos pos) {
        return fireState == TorchFireState.UNLIT;
    }

    @Override
    public boolean setOnFireDirectly(World world, BlockPos pos) {
        if (this.getCanBeSetOnFireDirectly(world, pos)) {

            if (!(world.getBlockEntity(pos) instanceof TorchBE be)) {
                return false;
            }

            if (!world.hasRain(pos)) {
                this.changeTorch(world, pos, world.getBlockState(pos), TorchFireState.LIT);
                Ignitable.playLitFX(world, pos);

                // Ensure the block entity has the required component
                ComponentMap components = be.getComponents();
                if (!components.contains(ModComponentsTypes.TORCH_FUEL)) {
                    ComponentMap updatedComponents = ComponentMap.builder()
                            .add(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent())
                            .build();
                    be.setComponents(updatedComponents);
                }
                be.markDirty();
            }
            else {
                Ignitable.playExtinguishSound(world, pos, false);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock() && isFuelHavingTorchBlock(newState)) {
            return;
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private boolean isFuelHavingTorchBlock(BlockState state) {
        return state.isOf(ModBlocks.CRUDE_TORCH_LIT)
                || state.isOf(ModBlocks.CRUDE_TORCH_SMOULDER)
                || state.isOf(ModBlocks.CRUDE_WALL_TORCH_LIT)
                || state.isOf(ModBlocks.CRUDE_WALL_TORCH_SMOULDER);
    }

    public void smoulder(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
        this.displaySharedParticles(state, world, pos);
        changeTorch(world, pos, state, TorchFireState.SMOULDER);
    }

    public void extinguish(World world, BlockPos pos, BlockState state) {
        world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
        this.displaySharedParticles(state, world, pos);
        changeTorch(world, pos, state, TorchFireState.BURNED_OUT);
    }

    public void burnOut(World world, BlockPos pos, BlockState state, boolean playSound) {
        if (playSound) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
        }
        this.displaySharedParticles(state, world, pos);
        changeTorch(world, pos, state, TorchFireState.BURNED_OUT);
    }

    public void light(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            Ignitable.playLitFX(world, pos);
            displayParticle(ParticleTypes.LAVA, state, world, pos);
            displayParticle(ParticleTypes.FLAME, state, world, pos);
            changeTorch(world, pos, state, TorchFireState.LIT);
        }
    }

    /** Particles that torches display on light up, smoulder and burn out **/
    private void displaySharedParticles(BlockState state, World world, BlockPos pos) {
        displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
        displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
        displayParticle(ParticleTypes.SMOKE, state, world, pos);
        displayParticle(ParticleTypes.SMOKE, state, world, pos);
    }

    public abstract boolean isWallTorch();

    public TorchFireState getFireState() {
        return fireState;
    }

    public void changeTorch(World world, BlockPos pos, BlockState oldState, TorchFireState newType) {
        if (world.isClient) return;

        // Grab the old BE's components before the block gets replaced
        ComponentMap oldComponents = null;
        if (world.getBlockEntity(pos) instanceof TorchBE oldBE) {
            oldComponents = oldBE.getComponents();
        }

        BlockState newState = isWallTorch()
                ? handler.getWallTorch(newType).getDefaultState().with(HorizontalFacingBlock.FACING, oldState.get(CrudeWallTorchBlock.FACING))
                : handler.getStandingTorch(newType).getDefaultState();

        // Replace the block
        world.setBlockState(pos, newState);

        // Transfer components (esp. torch fuel) into the new BE
        if (world.getBlockEntity(pos) instanceof TorchBE newBe && oldComponents != null) {
            TorchFuelComponent oldFuel = oldComponents.get(ModComponentsTypes.TORCH_FUEL);

            ComponentMap.Builder builder = ComponentMap.builder();
            if (oldFuel != null) {
                builder.add(ModComponentsTypes.TORCH_FUEL, new TorchFuelComponent(oldFuel.getFuel()));
            }
            // add other components here if needed

            newBe.setComponents(builder.build());
            newBe.markDirty();
        }
    }


    public static void displayParticle(ParticleEffect particle, BlockState state, World world, BlockPos pos, float spread)
    {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.7;
        double f = (double)pos.getZ() + 0.5;

        if (particle != null) {
            if (state.contains(Properties.HORIZONTAL_FACING)) {
                Direction dir = state.get(Properties.HORIZONTAL_FACING);
                Direction dir2 = dir.getOpposite();

                if (world instanceof ServerWorld) {
                    ((ServerWorld) world).spawnParticles(particle, d + 0.27 * (double) dir2.getOffsetX(), e + 0.22, f + 0.27 * (double) dir2.getOffsetZ(), 1, 0, 0, 0, 0);
                }
                else if (world.isClient) {
                    world.addParticle(particle, d + 0.27 * (double) dir2.getOffsetX(), e + 0.22, f + 0.27 * (double) dir2.getOffsetZ(), 0.0, 0.0, 0.0);
                }
            }
            else {
                if (world instanceof  ServerWorld) {
                    ((ServerWorld) world).spawnParticles(particle, d, e, f, 1, 0, 0, 0, 0);
                }
                else if (world.isClient) {
                    world.addParticle(particle, d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    public static void displayParticle(ParticleEffect particle, BlockState state, World world, BlockPos pos) {
        displayParticle(particle, state, world, pos, 0f);
    }

}