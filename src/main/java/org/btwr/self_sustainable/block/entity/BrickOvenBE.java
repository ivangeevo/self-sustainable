package org.btwr.self_sustainable.block.entity;

import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import org.btwr.self_sustainable.block.blocks.BrickOvenBlock;
import org.btwr.self_sustainable.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.btwr.self_sustainable.recipe.cooking.OvenCookingRecipe;
import org.btwr.shared_library.api.block.util.FireBlockUtils;

public class BrickOvenBE extends AbstractOvenBE implements Inventory {

    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override public void markDirty() {
            BrickOvenBE.this.markDirty();
        }
    };
    
    public BrickOvenBE(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
    }

    @Override
    public void tick() {
        boolean wasBurning = fuelBurnTime > 0;
        boolean inventoryChanged = false;

        if (wasBurning) {
            --fuelBurnTime;
        }

        if (world != null && !world.isClient) {
            if (wasBurning || lightOnNextUpdate) {
                fuelBurnTime += unlitFuelBurnTime;
                unlitFuelBurnTime = 0;
                lightOnNextUpdate = false;
            }

            if (isBurning() && canSmelt(world)) {
                if (cookTime == 0) {
                    cookTimeTotal = getRecipeFor(getCookStack())
                            .map(entry -> entry.value().getCookingTime())
                            .orElse(DEFAULT_COOK_TIME);
                }

                ++cookTime;

                if (cookTime >= cookTimeTotal) {
                    cookTime = 0;
                    smeltItem(world);
                    inventoryChanged = true;
                }
            } else {
                cookTime = 0;
            }

            if (isBurning() && world.getRandom().nextFloat() <= CHANCE_OF_FIRE_SPREAD) {
                BlockPos pos = new BlockPos(this.pos.getX(), this.pos.getY(), this.getPos().getZ());
                Direction facing = world.getBlockState(pos).get(BrickOvenBlock.FACING);

                pos.offset(facing);

                FireBlockUtils.checkForFireSpreadAndDestructionToOneBlockLocation(world, pos);
            }

            BrickOvenBlock ovenBlock = (BrickOvenBlock) world.getBlockState(pos).getBlock();

            if (wasBurning != isBurning()) {
                inventoryChanged = true;
                ovenBlock.updateOvenBlockState(isBurning(), (ServerWorld)world, pos);
            }

            if (mortarOnNextUpdate) {
                ovenBlock.updateOvenBlockState(isBurning(), (ServerWorld)world, pos);
                mortarOnNextUpdate = false;
            }

            //setCookStack(this.getCookStack());
            updateVisualFuelLevel();
        }

        if (inventoryChanged) {
            markDirty();
        }
    }

    public int getItemFuelTime(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        Item item = stack.getItem();

        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
    }

    /**
     * The item the player sees displayed in the oven.
     */
    public ItemStack getCookStack() {
        return inventory.getStack(0);
    }

    public void setCookStack(ItemStack stack) {
        inventory.setStack(0, stack.copy());
        if (world != null) {
            world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    public void addCookStack(ItemStack stack) {
        setCookStack(stack.copyWithCount(1));
        cookTime = 0;
        markDirty();
    }

    /**
     * Gives the item in the cook slot to the player (or drops it toward the
     * oven's facing direction), then clears the slot.
     */
    public void givePlayerCookStack(World world, PlayerEntity player, Direction facing) {
        // Drop the item in the oven towards facing if it can't be picked up by a player
        if (!world.isClient) {
            if (!getCookStack().isEmpty()) {
                if (!player.getInventory().insertStack(getCookStack().copy())) {
                    Block.dropStack(world, pos.offset(facing), getCookStack().copy());
                }
            }
        }
        inventory.clear();
        markDirty();
    }



    public boolean hasValidFuel() {
        return unlitFuelBurnTime > 0;
    }

    public boolean applyMortar() {
        mortarOnNextUpdate = true;
        return true;
    }

    public boolean canSmelt(World world) {
        ItemStack input = getCookStack();
        if (input.isEmpty()) return false;

        return !getSmeltResult(world, input).isEmpty();
    }

    private void smeltItem(World world) {
        if (!canSmelt(world)) return;
        ItemStack result = getSmeltResult(world, this.getCookStack());
        setCookStack(result);
        if (!world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    private ItemStack getSmeltResult(World world, ItemStack input) {
        return world.getRecipeManager()
                .getFirstMatch(OvenCookingRecipe.Type.INSTANCE, new SingleStackRecipeInput(input), world)
                .map(entry -> entry.value().getResult(world.getRegistryManager()))
                .orElse(ItemStack.EMPTY);
    }

    private void readCookStackNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        if (!nbt.contains("CookStack", 10)) return;
        setCookStack(ItemStack.CODEC.parse(registryLookup.getOps(NbtOps.INSTANCE), nbt.getCompound("CookStack"))
                .result()
                .orElse(ItemStack.EMPTY));
    }

    private void writeCookStackNbt(NbtCompound nbt, ItemStack cookStack, RegistryWrapper.WrapperLookup registryLookup) {
        if (cookStack.isEmpty()) return;
        nbt.put("CookStack", cookStack.encode(registryLookup, new NbtCompound()));
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound nbtCompound = createNbt(registryLookup);
        this.writeCookStackNbt(nbtCompound, this.getCookStack(), registryLookup);
        nbtCompound.putByte("VisualFuelLevel", (byte) visualFuelLevel);
        return nbtCompound;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        this.writeCookStackNbt(nbt, this.getCookStack(), registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.readCookStackNbt(nbt, registryLookup);
    }

    static void setParticles(World world, BlockPos pos, BlockState state) {
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
    }

    // Inventory related methods
    @Override public int size() {
        return inventory.size();
    }
    @Override public boolean isEmpty() {
        return inventory.isEmpty();
    }
    @Override public ItemStack getStack(int slot) {
        return inventory.getStack(slot);
    }
    @Override public ItemStack removeStack(int slot, int amount) {
        return inventory.removeStack(slot, amount);
    }
    @Override public ItemStack removeStack(int slot) {
        return inventory.removeStack(slot);
    }
    @Override public void setStack(int slot, ItemStack stack) {
        inventory.setStack(slot, stack);
    }
    @Override public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }
    @Override public void clear() {
        inventory.clear();
    }

}