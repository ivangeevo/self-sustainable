package net.ivangeevo.self_sustainable.block.entity;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.ivangeevo.self_sustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.self_sustainable.block.interfaces.Ignitable;
import net.ivangeevo.self_sustainable.entity.ModBlockEntities;
import net.ivangeevo.self_sustainable.entity.util.SingleStackInventory;
import net.ivangeevo.self_sustainable.recipe.OvenCookingRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class BrickOvenBE extends BlockEntity implements Ignitable, SingleStackInventory
{
    int unlitFuelBurnTime;
    int fuelBurnTime;
    int cookTime = 0;
    int cookTimeTotal = 0;
    private boolean lightOnNextUpdate = false;
    public static final int DEFAULT_COOK_TIME = 400;
    private final int cookTimeMultiplier = 4;
    protected ItemStack cookStack = ItemStack.EMPTY;

    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();

    public final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter = RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);

    static private final float CHANCE_OF_FIRE_SPREAD = 0.01F;
    public static final int BASE_BURN_TIME_MULTIPLIER = 2;
    private final int brickBurnTimeMultiplier = 4; // applied on top of base multiplier of standard furnace

    private final int maxFuelBurnTime = ((64 + 7) * 25 * 2 * brickBurnTimeMultiplier); // 64 + 7 buffer, 25x saw dust, 2x base furnace multiplier

    private final int visualFuelLevelIncrement = (200 * 2 * brickBurnTimeMultiplier);
    private final int visualSputterFuelLevel = (visualFuelLevelIncrement / 4);
    private int visualFuelLevel;

    public BrickOvenBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
        this.visualFuelLevel = state.get(BrickOvenBlock.FUEL_LEVEL);
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return this.matchGetter.getFirstMatch(new SimpleInventory(stack), this.world);
    }


    public static void serverTick(World world, BlockPos pos, BlockState state, @NotNull BrickOvenBE ovenBE)
    {
        ItemStack cookStack = ovenBE.getStack();
        boolean bInvChanged = false;

        // Decrease furnace burn time if it's still burning
        if (ovenBE.fuelBurnTime > 0)
        {
            --ovenBE.fuelBurnTime;
        }

        if (!cookStack.isEmpty())
        {
            bInvChanged = true;

            // Increment the cooking time for the first item
            ovenBE.cookTime = ovenBE.cookTime + 1;

            SimpleInventory inventory = new SimpleInventory(cookStack);
            ItemStack cookedStack = ovenBE.matchGetter.getFirstMatch(inventory, world)
                    .map(recipe -> recipe.craft(inventory, world.getRegistryManager()))
                    .orElse(cookStack);

            if (ovenBE.cookTime >= ovenBE.cookTimeTotal && cookedStack.isItemEnabled(world.getEnabledFeatures())) {
                ovenBE.setStack(cookedStack);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
            }
        }

        if (bInvChanged) {
            markDirty(world, pos, state);
        }
    }

    protected static void markDirty(World world, BlockPos pos, BlockState state) {
        world.markDirty(pos);
        if (!state.isAir()) {
            world.updateComparators(pos, state.getBlock());
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBE oven) {
        setLargeSmokeParticles(world, pos, state, oven);
        setFlameParticles(world, pos, state);
    }

    private static void setLargeSmokeParticles(World world, BlockPos pos, BlockState state, BrickOvenBE ovenBE)
    {
        Random random = world.random;

        boolean hasItemToCook = false;

        for (Direction direction : Direction.Type.HORIZONTAL)
        {

            if (state.get(LIT) && !hasItemToCook && !ovenBE.cookStack.isEmpty() && random.nextFloat() < 0.2f)
            {
                double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.25f)
                        + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125f);
                double e = Math.max(pos.getY() + 0.5, Math.min(pos.getY() + 1.0, (double) pos.getY() + 0.7));
                double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.25f)
                        + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125f);

                for (int k = 0; k < 4; ++k) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                }

                hasItemToCook = true; // Set the boolean to true to avoid spawning more than one set of particles
            }
        }
    }

    private static void setFlameParticles(World world, BlockPos pos, BlockState state) {
        if (!state.get(LIT))
        {
            return;
        }

        double d = (double) pos.getX() + 0.5;
        double e = pos.getY();
        double f = (double) pos.getZ() + 0.5;

        if (world.getRandom().nextDouble() < 0.05)
        {
            world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE,
                    SoundCategory.BLOCKS, 1.0f, 1.0f, false);
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
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.cookStack = ItemStack.EMPTY;
        this.visualFuelLevel = nbt.getInt("VisualFuelLevel");

        if (nbt.contains("CookStack"))
        {
            cookStack = ItemStack.fromNbt(nbt.getCompound("CookStack"));
        }
        this.unlitFuelBurnTime = nbt.getShort("UnlitFuelBurnTime");
        this.fuelBurnTime = nbt.getShort("FuelBurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");

        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(string), nbtCompound.getInt(string));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putShort("VisualFuelLevel", (short) this.visualFuelLevel);
        nbt.putShort("UnlitFuelBurnTime", (short) this.unlitFuelBurnTime);
        nbt.putShort("FuelBurnTime", (short) this.fuelBurnTime);
        nbt.putShort("CookTime", (short) this.cookTime);
        nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
        this.writeCookStackNbt(nbt, this.cookStack);
        NbtCompound nbtCompound = new NbtCompound();
        this.recipesUsed.forEach((identifier, count) -> nbtCompound.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbtCompound);
    }

    private void writeCookStackNbt(NbtCompound nbt, ItemStack stack) {
        if (!stack.isEmpty()) {
            NbtCompound stackNbt = new NbtCompound();
            stack.writeNbt(stackNbt);
            nbt.put("CookStack", stackNbt);
        } else {
            nbt.put("CookStack", new NbtCompound()); // Empty compound to indicate empty ItemStack
        }
    }


    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public int getVisualFuelLevel() {
        return visualFuelLevel;
    }

    private boolean isBurning() {
        return this.fuelBurnTime > 0;
    }

    public void setVisualFuelLevel(int visualFuelLevel) {
        this.visualFuelLevel = visualFuelLevel;
        markDirty();  // Mark the block entity as changed
    }

    public boolean addItem(Entity user, ItemStack stack, int cookTime)
    {

            this.cookTimeTotal = cookTime;
            this.cookTime = 0;
            this.cookStack = stack.split(1);
            this.getWorld().emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
            this.updateListeners();

            return true;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void retrieveItem(World world, PlayerEntity player)
    {
        ItemStack cookStack = getStack();

        if (!cookStack.isEmpty() && !world.isClient())
        {
            if (!getStack().isEmpty())
            {
                player.giveItemStack(getStack());
                setStack(ItemStack.EMPTY);
                markDirty();
                Objects.requireNonNull(this.getWorld()).updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public boolean isEmpty() {
        return cookStack.isEmpty();
    }
    @Override
    public ItemStack getStack() {
        return cookStack;
    }
    @Override
    public ItemStack removeStack()
    {
        return cookStack = ItemStack.EMPTY;
    }
    @Override
    public void setStack(ItemStack newStack)
    {
        cookStack = newStack;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity var1) {
        return true;
    }


    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        writeCookStackNbt(nbtCompound, this.cookStack);
        return nbtCompound;
    }


    @Override
    public void clear() {

    }
}
