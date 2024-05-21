// FCMOD

package net.ivangeevo.self_sustainable.util;


import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class ItemUtils {

    static public void ejectStackWithRandomOffset(World world, BlockPos pos, ItemStack stack) {
        float xOffset = world.getRandom().nextFloat() * 0.7F + 0.15F;
        float yOffset = world.getRandom().nextFloat() * 0.2F + 0.1F;
        float zOffset = world.getRandom().nextFloat() * 0.7F + 0.15F;

        ejectStackWithRandomVelocity(world, (float) pos.getX() + xOffset, (float) pos.getY() + yOffset, (float) pos.getZ() + zOffset, stack);
    }

    static public void ejectSingleItemWithRandomOffset(World world, BlockPos pos, int iShiftedItemIndex) {
        Item item = Registries.ITEM.get(iShiftedItemIndex);
        ItemConvertible itemConvertible = item.asItem();

        ItemStack itemStack = new ItemStack(itemConvertible, 1);

        ejectStackWithRandomOffset(world, pos, itemStack);
    }


    public static void ejectStackWithRandomVelocity(World world, double x, double y, double z, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(world, x, y, z, stack);

        float velocityFactor = 0.05F;

        itemEntity.setVelocity(
                world.random.nextGaussian() * velocityFactor,
                world.random.nextGaussian() * velocityFactor + 0.2F,
                world.random.nextGaussian() * velocityFactor
        );

        itemEntity.setPickupDelay(10);

        world.spawnEntity(itemEntity);
    }

    static public void ejectSingleItemWithRandomVelocity(World world, float xPos, float yPos, float zPos, int iShiftedItemIndex, int iDamage) {
        Item item = Registries.ITEM.get(iShiftedItemIndex);

        ItemConvertible itemConvertible = item.asItem();

        ItemStack itemStack = new ItemStack(itemConvertible, 1);

        ItemUtils.ejectStackWithRandomVelocity(world, xPos, yPos, zPos, itemStack);
    }

    static public void dropStackAsIfBlockHarvested(World world, BlockPos pos, ItemStack stack) {
        if (!world.isClient && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
            double d = 0.5D;
            double d1 = world.random.nextFloat() * 0.8F + 0.1F;
            double d2 = world.random.nextFloat() * 0.8F + 0.1F;
            double d3 = world.random.nextFloat() * 0.8F + 0.1F;

            ItemEntity entityitem = new ItemEntity(world, pos.getX() + d, pos.getY() + d1, pos.getZ() + d2, stack);
            entityitem.setPos(
                    world.random.nextGaussian() * 0.05D,
                    world.random.nextGaussian() * 0.05D + 0.2D,
                    world.random.nextGaussian() * 0.05D
            );

            world.spawnEntity(entityitem);
        }
    }

    static public void dropSingleItemAsIfBlockHarvested(World world, BlockPos pos, int iShiftedItemIndex, int iDamage) {
        Item item = Registries.ITEM.get(iShiftedItemIndex);

        ItemConvertible itemConvertible = item.asItem();

        ItemStack itemStack = new ItemStack(itemConvertible, 1);

        ItemUtils.dropStackAsIfBlockHarvested(world, pos, itemStack);
    }


    static public void ejectStackFromBlockTowardsFacing(World world, BlockPos pos, BlockState blockState, ItemStack stack, Direction direction) {
        Vec3d ejectPos = new Vec3d(
                world.getRandom().nextDouble() * 0.7D + 0.15D,
                1.2D + world.getRandom().nextDouble() * 0.1D,
                world.getRandom().nextDouble() * 0.7D + 0.15D);

        double x = ejectPos.x;


        // Tilting of the ejectPos should happen here
        ejectPos = VectorUtils.tiltVector(ejectPos, direction.getId());


        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)world)
                .add(LootContextParameters.ORIGIN, pos.toCenterPos())
                .add(LootContextParameters.TOOL, stack)
                .addOptional(LootContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos));


        List<ItemStack> drops = blockState.getDroppedStacks(builder);

        for (ItemStack droppedItems : drops) {
            ItemEntity entity = new ItemEntity(world, pos.getX() + ejectPos.getX(), pos.getY() + ejectPos.getY(), pos.getZ() + ejectPos.getZ(), droppedItems);

            spawnItemEntity(world, () -> entity , direction);
        }


        blockState.onStacksDropped((ServerWorld) world, pos, stack, false);    }

    private static void spawnItemEntity(World world, Supplier<ItemEntity> itemEntitySupplier, Direction direction) {
        ItemEntity entity = itemEntitySupplier.get();

        int iFacing = direction.getId();

        if (iFacing < 2) {
            entity.lastRenderX = world.getRandom().nextDouble() * 0.1D - 0.05D;
            entity.lastRenderZ = world.getRandom().nextDouble() * 0.1D - 0.05D;

            if (iFacing == 0) {
                entity.lastRenderY = 0D;
            } else {
                entity.lastRenderY = 0.2D;
            }

        } else {
            Vec3d ejectVel = new Vec3d(world.getRandom().nextDouble() * 0.1D - 0.05D,
                    0.2D, world.getRandom().nextDouble() * -0.05D - 0.05D);

            ejectVel.rotateY(direction.getId());

            entity.lastRenderX = ejectVel.x;
            entity.lastRenderY = ejectVel.y;
            entity.lastRenderZ = ejectVel.z;
        }


        entity.setToDefaultPickupDelay();


        world.spawnEntity(entity);
    }




    /**
     * Yaws the vector around the origin of the J axis. Assumes that the initial facing is along the negative K axis (facing 2).
     */
    public void rotateAsVectorAroundJToFacing(Vec3d vector, int iFacing) {
        if (iFacing > 2) {
            if (iFacing == 5) // i + 1
            {
                double tempZ = vector.x;

                vector = new Vec3d(-vector.z, vector.y, tempZ);
            } else if (iFacing == 4) // i - 1
            {
                double tempZ = -vector.x;

                vector = new Vec3d(vector.z, vector.y, tempZ);
            } else // if ( iFacing == 3 ) // k + 1
            {
                vector = new Vec3d(-vector.x, -vector.y, -vector.z);
            }
        }
    }




    static public void givePlayerStackOrEjectFromTowardsFacing(PlayerEntity player, BlockState state, ItemStack stack, BlockPos pos, Direction direction)
    {
        if ( player.getInventory().insertStack( stack ) )
        {
            player.getWorld().playSoundFromEntity( player,null,SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F,
                    ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        }
        else if ( !player.getWorld().isClient )
        {
            ItemUtils.ejectStackFromBlockTowardsFacing(player.getWorld(), pos, state, stack, direction);
        }
    }

    static public void givePlayerStackOrEjectFavorEmptyHand(PlayerEntity player, ItemStack stack,BlockPos pos)
    {
        if ( player.giveItemStack(stack) )
        {
            BlockPos thisPos = player.getBlockPos();
            float pitchChance = (((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.getWorld().playSound( player, thisPos, SoundEvents.BLOCK_LAVA_POP,SoundCategory.PLAYERS, 0.2F, pitchChance);

        }
        else
        {
            givePlayerStackOrEject(player, stack, pos);
        }
    }

    static public void givePlayerStackOrEject(PlayerEntity player, ItemStack stack, BlockPos pos)
    {
        if ( player.getInventory().insertStack( stack ) )
        {
            float pitchChance = (((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.getWorld().playSoundFromEntity( player,null, SoundEvents.BLOCK_LAVA_POP, SoundCategory.PLAYERS, 0.2F, pitchChance);
        }
        else if ( !player.getWorld().isClient)
        {
            ItemUtils.ejectStackWithRandomOffset(player.getWorld(), pos, stack);
        }
    }

    static public void givePlayerStackOrEject(PlayerEntity player, ItemStack stack)
    {
        if ( player.getInventory().insertStack( stack ) )
        {
            player.getWorld().playSoundFromEntity( player, null, SoundEvents.BLOCK_LAVA_POP,SoundCategory.BLOCKS, 0.2F,
                    1f);
        }
        else if ( !player.getWorld().isClient() )
        {
            ItemUtils.ejectStackWithRandomVelocity(player.getWorld(), player.getX(), player.getY(), player.getZ(), stack);
        }
    }

    }
