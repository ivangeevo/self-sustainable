package net.ivangeevo.self_sustainable.mixin;

import net.ivangeevo.self_sustainable.block.entity.util.CampfireExtinguisher;
import net.ivangeevo.self_sustainable.block.interfaces.CampfireBlockEntityAdded;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntity.class)
public abstract class CampfireBlockEntityMixin extends BlockEntity implements CampfireBlockEntityAdded
{

    @Shadow protected abstract void updateListeners();
    @Unique
    private int litTime = 0;


    @Unique private static ItemStack itemBeingCooked = ItemStack.EMPTY;
    @Unique private static int cookingTime = 0;
    @Unique private static int cookingTotalTime = 0;

    @Override
    public int getLitTime() {
        return litTime;
    }

    @Override
    public void setLitTime(int value) {
        this.litTime = value;
    }

    @Override
    public int getCookTime() {
        return cookingTime;
    }

    @Override
    public int setCookTime(int value) {
        return cookingTime = value;
    }

    @Override
    public int getTotalCookTime() {
        return cookingTotalTime;
    }

    @Override
    public int setTotalCookTime(int value) {
        return cookingTotalTime = value;
    }
    @Override
    public ItemStack getItemBeingCooked() {
        return itemBeingCooked;
    }
    @Override
    public void setItemBeingCooked(ItemStack newStack) {
        itemBeingCooked = newStack;
    }

    @Unique private static final RecipeManager.MatchGetter<Inventory, CampfireCookingRecipe> recipeMatchGetter =
            RecipeManager.createCachedMatchGetter(RecipeType.CAMPFIRE_COOKING);

    public CampfireBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Inject(method = "litServerTick", at = @At("HEAD"), cancellable = true)
    private static void injectedExtinguishLogic(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        // Adding burn logic and respectively extinguishing it.
        CampfireExtinguisher.onLitServerTick(world, pos, state, campfire);

        // Use this cast because it provides access to the new variables.
        CampfireBlockEntityAdded entity;
        entity = (CampfireBlockEntityAdded) campfire;

        boolean bl = false;
        if (!entity.getItemBeingCooked().isEmpty()) {
            bl = true;
            entity.setCookTime(cookingTime += 1);
            if (entity.getCookTime() >= entity.getTotalCookTime()) {
                SimpleInventory inventory = new SimpleInventory(entity.getItemBeingCooked());
                ItemStack resultStack = recipeMatchGetter.getFirstMatch(inventory, world)
                        .map(recipe -> recipe.craft(inventory, world.getRegistryManager()))
                        .orElse(entity.getItemBeingCooked());

                if (resultStack.isItemEnabled(world.getEnabledFeatures())) {
                    entity.setItemBeingCooked(resultStack);  // Set the cooked item in place of the raw item
                    entity.setCookTime(0);
                    entity.setTotalCookTime(0);
                    world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                    world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(state));
                }
            }
        }
        if (bl) {
            CampfireBlockEntity.markDirty(world, pos, state);
        }
        ci.cancel();

    }

    @Inject(method = "unlitServerTick", at = @At("HEAD"), cancellable = true)
    private static void injectedUnlitServerTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        boolean bl = false;

        // Use this cast because it has all new variables in its scope.
        CampfireBlockEntityAdded entity;
        entity = (CampfireBlockEntityAdded) campfire;

        if (!entity.getItemBeingCooked().isEmpty())
        {
            if (entity.getCookTime() > 0) {
                bl = true;
                entity.setCookTime(MathHelper.clamp(entity.getCookTime() - 2, 0, entity.getTotalCookTime()));
            }
        }

        if (bl) {
            markDirty(world, pos, state);
        }
        ci.cancel();
    }

    @Inject(method = "clientTick", at = @At("HEAD"), cancellable = true)
    private static void injectedClientTick(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci)
    {
        Random random = world.random;

        // Check for smoke particle spawn chance
        if (random.nextFloat() < 0.11F)
        {
            // Determine the number of particles to spawn
            int particleCount = random.nextInt(2) + 2;

            for (int i = 0; i < particleCount; ++i)
            {
                // define position of the particles
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 0.9; // 0.1 below the ceiling
                double z = pos.getZ() + 0.5;

                // Spawn the smoke particle
                world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
            }
        }

        // Handle additional logic for particles when items are being cooked
        if (!((CampfireBlockEntityAdded) campfire).getItemBeingCooked().isEmpty() && random.nextFloat() < 0.2F) {
            // Get the direction the campfire is facing
            Direction direction = state.get(CampfireBlock.FACING);
            // Adjust particle spawn location based on the direction
            float offset = 0.3125F;
            double d = pos.getX() + 0.5 - direction.getOffsetX() * offset + direction.rotateYClockwise().getOffsetX() * offset;
            double e = pos.getY() + 0.9; // 0.1 below the ceiling
            double g = pos.getZ() + 0.5 - direction.getOffsetZ() * offset + direction.rotateYClockwise().getOffsetZ() * offset;

            world.addParticle(ParticleTypes.SMOKE, d, e, g, 0.0, 5.0E-4, 0.0);
        }

        ci.cancel();
    }


    @Inject(method = "readNbt", at = @At("RETURN"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci)
    {
        if (nbt.contains("LitTime"))
        {
            litTime = nbt.getInt("LitTime");
        }
    }

    @Inject(method = "writeNbt", at = @At("RETURN"))
    private void writeNbt(NbtCompound nbt, CallbackInfo ci)
    {
        nbt.putInt("LitTime", litTime);
    }

    @Nullable @Override
    public ItemStack retrieveItem(@Nullable Entity user)
    {
            ItemStack stack = this.getItemBeingCooked();
            if (!stack.isEmpty())
            {
                // Optionally handle different retrieval logic here
                setCookTime(0);
                setItemBeingCooked(ItemStack.EMPTY); // Clear the slot
                assert this.world != null;
                this.world.emitGameEvent(GameEvent.BLOCK_CHANGE, this.getPos(), GameEvent.Emitter.of(user, this.getCachedState()));
                this.updateListeners();

                // Return a copy of the retrieved item
                return stack.copy();
            }
        return ItemStack.EMPTY; // Return an empty stack if no item was retrieved
    }

}
