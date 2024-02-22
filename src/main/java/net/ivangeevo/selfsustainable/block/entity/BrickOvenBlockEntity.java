/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.ivangeevo.selfsustainable.block.entity;

import net.ivangeevo.selfsustainable.block.blocks.BrickOvenBlock;
import net.ivangeevo.selfsustainable.entity.ModBlockEntities;
import net.ivangeevo.selfsustainable.recipe.OvenCookingRecipe;
import net.ivangeevo.selfsustainable.util.ItemUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Clearable;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BrickOvenBlockEntity extends BlockEntity implements Clearable {
    private final DefaultedList<ItemStack> itemsBeingCooked = DefaultedList.ofSize(1, ItemStack.EMPTY);;
    private final int[] cookingTimes;
    private final int[] cookingTotalTimes;
    private final RecipeManager.MatchGetter<Inventory, OvenCookingRecipe> matchGetter =  RecipeManager.createCachedMatchGetter(OvenCookingRecipe.Type.INSTANCE);

    // Added variables from BTW
    private ItemStack cookStack = null;

    protected ItemStack[] furnaceItemStacks = new ItemStack[3];



    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OVEN_BRICK, pos, state);
        this.cookingTimes = new int[4];
        this.cookingTotalTimes = new int[4];
    }

    public void givePlayerCookStack(World world,  BlockPos pos, BlockState state, PlayerEntity player, Direction facing)
    {
        if (!world.isClient)
        {
            // this is legacy support to clear all inventory items that may have been added through the GUI

            ejectAllNotCookStacksToFacing(world,pos, state, player, facing);
        }

        ItemUtils.givePlayerStackOrEjectFromTowardsFacing(player, state, cookStack, pos, facing);

        furnaceItemStacks[0] = null;
        furnaceItemStacks[1] = null;
        furnaceItemStacks[2] = null;

        setCookStack(null);
    }

    private void ejectAllNotCookStacksToFacing(World world, BlockPos pos, BlockState state, PlayerEntity player, Direction facing)
    {



        if ( furnaceItemStacks[0] != null && !ItemStack.areEqual(furnaceItemStacks[0], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos,state, furnaceItemStacks[0], facing);

            furnaceItemStacks[0] = null;
        }

        if ( furnaceItemStacks[1] != null && !ItemStack.areEqual(furnaceItemStacks[1], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, state, cookStack, facing);

            furnaceItemStacks[1] = null;
        }

        if ( furnaceItemStacks[2] != null && !ItemStack.areEqual(furnaceItemStacks[2], cookStack) )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(world, pos, state, furnaceItemStacks[2], facing);

            furnaceItemStacks[2] = null;
        }

        markDirty();
    }

    public void setCookStack(ItemStack stack)
    {
        if ( stack != null )
        {
            cookStack = stack.copy();
        }
        else
        {
            cookStack = null;
        }

        if (world != null && !world.isClient) {
            BlockPos pos = getPos();
            world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
    }

    public static void litServerTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        boolean bl = false;

        for (int i = 0; i < oven.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = oven.itemsBeingCooked.get(i);
            if (!itemStack.isEmpty()) {
                bl = true;
                int var10002 = oven.cookingTimes[i]++;
                if (oven.cookingTimes[i] >= oven.cookingTotalTimes[i]) {
                    Inventory inventory = new SimpleInventory(itemStack);
                    ItemStack itemStack2 = oven.matchGetter.getFirstMatch(inventory, world).map((recipe) ->
                            recipe.craft(inventory, world.getRegistryManager())).orElse(itemStack);
                    if (itemStack2.isItemEnabled(world.getEnabledFeatures())) {
                        ItemScatterer.spawn(world, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), itemStack2);
                        oven.itemsBeingCooked.set(i, ItemStack.EMPTY);
                        world.updateListeners(pos, state, state, 3);
                        world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                    }
                }
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }

    }

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    public static void unlitServerTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        boolean bl = false;

        for (int i = 0; i < oven.itemsBeingCooked.size(); ++i) {
            if (oven.cookingTimes[i] > 0) {
                bl = true;
                oven.cookingTimes[i] = MathHelper.clamp(oven.cookingTimes[i] - 2, 0, oven.cookingTotalTimes[i]);
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }

    }

    public static void clientTick(World world, BlockPos pos, BlockState state, BrickOvenBlockEntity oven) {
        Random random = world.random;
        int i;
        if (random.nextFloat() < 0.11F) {
            for (i = 0; i < random.nextInt(2) + 2; ++i) {
                CampfireBlock.spawnSmokeParticle(world, pos, state.get(CampfireBlock.SIGNAL_FIRE), false);
            }
        }

        i = state.get(BrickOvenBlock.FACING).getHorizontal();

        for (int j = 0; j < oven.itemsBeingCooked.size(); ++j) {
            if (!oven.itemsBeingCooked.get(j).isEmpty() && random.nextFloat() < 0.2F) {
                Direction direction = Direction.fromHorizontal(Math.floorMod(j + i, 4));
                float f = 0.3125F;
                double d = (double) pos.getX() + 0.5 - (double) ((float) direction.getOffsetX() * 0.3125F) + (double) ((float) direction.rotateYClockwise().getOffsetX() * 0.3125F);
                double e = (double) pos.getY() + 0.5;
                double g = (double) pos.getZ() + 0.5 - (double) ((float) direction.getOffsetZ() * 0.3125F) + (double) ((float) direction.rotateYClockwise().getOffsetZ() * 0.3125F);

                for (int k = 0; k < 4; ++k) {
                    world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
                }
            }
        }

    }

    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.itemsBeingCooked.clear();
        Inventories.readNbt(nbt, this.itemsBeingCooked);
        int[] is;
        if (nbt.contains("CookingTimes", 11)) {
            is = nbt.getIntArray("CookingTimes");
            System.arraycopy(is, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

        if (nbt.contains("CookingTotalTimes", 11)) {
            is = nbt.getIntArray("CookingTotalTimes");
            System.arraycopy(is, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, is.length));
        }

    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.itemsBeingCooked, true);
        nbt.putIntArray("CookingTimes", this.cookingTimes);
        nbt.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.itemsBeingCooked, true);
        return nbtCompound;
    }

    public Optional<OvenCookingRecipe> getRecipeFor(ItemStack stack) {
        return this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.matchGetter.getFirstMatch(new SimpleInventory(new ItemStack[]{stack}), this.world);
    }

    public boolean addItem(@Nullable Entity user, ItemStack stack, int cookTime) {
        for (int i = 0; i < this.itemsBeingCooked.size(); ++i) {
            ItemStack itemStack = (ItemStack) this.itemsBeingCooked.get(i);
            if (itemStack.isEmpty()) {
                this.cookingTotalTimes[i] = cookTime;
                this.cookingTimes[i] = 0;
                this.itemsBeingCooked.set(i, stack.split(1));
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();
                return true;
            }
        }

        return false;
    }

    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    public void clear() {
        this.itemsBeingCooked.clear();
    }

    public void spawnItemsBeingCooked() {
        if (this.world != null) {
            this.updateListeners();
        }

    }
}
