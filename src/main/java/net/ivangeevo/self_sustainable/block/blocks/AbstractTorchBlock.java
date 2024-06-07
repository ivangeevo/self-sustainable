package net.ivangeevo.self_sustainable.block.blocks;

import com.terraformersmc.modmenu.util.mod.Mod;
import net.ivangeevo.self_sustainable.block.entity.TorchBE;
import net.ivangeevo.self_sustainable.block.entity.util.IFuelBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.item.items.TorchItem;
import net.ivangeevo.self_sustainable.tag.ModTags;
import net.ivangeevo.self_sustainable.util.ModTorchHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.IntSupplier;

public abstract class AbstractTorchBlock extends BlockWithEntity implements BlockEntityProvider, IFuelBlock
{

    public ParticleEffect particle;
    public TorchFireState fireState;
    public ModTorchHandler handler;
    public IntSupplier maxFuel;

    public AbstractTorchBlock(AbstractBlock.Settings settings, ParticleEffect particle, TorchFireState fireLevel, IntSupplier maxFuel)
    {
        super(settings);
        this.particle = particle;
        this.fireState = fireLevel;
        this.maxFuel = maxFuel;
    }

    public void smother(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            changeTorch(world, pos, state, TorchFireState.SMOULDER);
        }
    }

    public void extinguish(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            changeTorch(world, pos, state, TorchFireState.UNLIT);
        }
    }

    public void burnOut(World world, BlockPos pos, BlockState state, boolean playSound)
    {
        if (!world.isClient)
        {
            if (playSound) world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1f);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.LARGE_SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
            changeTorch(world, pos, state, TorchFireState.BURNED_OUT);
        }
    }

    public void light(World world, BlockPos pos, BlockState state)
    {
        if (!world.isClient)
        {
            Ignitable.playLitFX(world, pos);
            displayParticle(ParticleTypes.LAVA, state, world, pos);
            displayParticle(ParticleTypes.FLAME, state, world, pos);
            changeTorch(world, pos, state, TorchFireState.LIT);
        }
    }

    public abstract boolean isWall();

    public TorchFireState getFireState()
    {
        return fireState;
    }

    public void changeTorch(World world, BlockPos pos, BlockState curState, TorchFireState newType) {
        BlockState newState;

        if (isWall())
        {
            newState = handler.getWallTorch(newType).getDefaultState().with(HorizontalFacingBlock.FACING, curState.get(ModWallTorchBlock.FACING));
        }
        else
        {
            newState = handler.getStandingTorch(newType).getDefaultState();
        }

        int newFuel = 0;
        if (world.getBlockEntity(pos) != null) newFuel = ((TorchBE) Objects.requireNonNull(world.getBlockEntity(pos))).getFuel();
        world.setBlockState(pos, newState);
        if (world.getBlockEntity(pos) != null) ((TorchBE) Objects.requireNonNull(world.getBlockEntity(pos))).setFuel(newFuel);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new TorchBE(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
       return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return checkType(type, ModBlockEntities.TORCH, TorchBE::tick);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
    {
        if (fireState == TorchFireState.LIT || fireState == TorchFireState.SMOULDER)
        {
            displayParticle(ParticleTypes.SMOKE, state, world, pos);
        }

        if (fireState == TorchFireState.LIT) {
            displayParticle(this.particle, state, world, pos);
        }
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getStackInHand(hand);
        boolean success = false;

        if (fireState == TorchFireState.LIT) {
            if (attemptUse(stack, player, hand, ModTags.Items.EXTINGUISH_TORCHES_ON_USE)) {
                extinguish(world, pos, state);
                player.swingHand(hand);
                return ActionResult.SUCCESS;
            }

            if (attemptUse(stack, player, hand, ModTags.Items.SMOTHER_TORCHES_ON_USE)) {
                smother(world, pos, state);
                player.swingHand(hand);
                return ActionResult.SUCCESS;
            }
        }

        if (fireState == TorchFireState.SMOULDER || fireState == TorchFireState.UNLIT) {
            if (attemptUse(stack, player, hand, ModTags.Items.TORCHES_CAN_LIGHT_UP)) {
                light(world, pos, state);
                player.swingHand(hand);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof TorchBE && itemStack.getItem() instanceof TorchItem) {
            int fuel = TorchItem.getFuel(itemStack);

            if (fuel == 0) {
                ((TorchBE) be).setFuel(48000);
            } else {
                ((TorchBE) be).setFuel(fuel);
            }
        }
    }
    @Override
    public void outOfFuel(World world, BlockPos pos, BlockState state, boolean playSound) {
        burnOut(world, pos, state, playSound);
    }

    public static boolean canLight(Item item, BlockState blockState)
    {

        if (item instanceof TorchItem)
        {
            TorchFireState state = ((TorchItem) item).getTorchState();

            if (state == TorchFireState.UNLIT || state == TorchFireState.SMOULDER)
            {
                return blockState.isIn(ModTags.Blocks.DIRECTLY_IGNITABLE_FROM_ON_USE);
            }
        }

        return false;
    }

    public static void displayParticle(ParticleEffect particle, BlockState state, World world, BlockPos pos, float spread)
    {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.7;
        double f = (double)pos.getZ() + 0.5;

        if (particle != null)
        {
            if (state.contains(Properties.HORIZONTAL_FACING))
            {
                Direction dir = state.get(Properties.HORIZONTAL_FACING);
                Direction dir2 = dir.getOpposite();

                if (world instanceof ServerWorld)
                {
                    ((ServerWorld) world).spawnParticles(particle, d + 0.27 * (double) dir2.getOffsetX(), e + 0.22, f + 0.27 * (double) dir2.getOffsetZ(), 1, 0, 0, 0, 0);
                }
                else if (world.isClient)
                {
                    world.addParticle(particle, d + 0.27 * (double) dir2.getOffsetX(), e + 0.22, f + 0.27 * (double) dir2.getOffsetZ(), 0.0, 0.0, 0.0);
                }
            }
            else
            {
                if (world instanceof  ServerWorld)
                {
                    ((ServerWorld) world).spawnParticles(particle, d, e, f, 1, 0, 0, 0, 0);
                }
                else if (world.isClient)
                {
                    world.addParticle(particle, d, e, f, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    public static void displayParticle(ParticleEffect particle, BlockState state, World world, BlockPos pos) {
        displayParticle(particle, state, world, pos, 0f);
    }


}