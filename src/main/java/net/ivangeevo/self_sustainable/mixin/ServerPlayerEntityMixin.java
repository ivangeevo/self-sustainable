package net.ivangeevo.self_sustainable.mixin;

import com.mojang.authlib.GameProfile;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity
{

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }


    @Unique private static Random random = new Random();

/**
    //@Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if ( !this.getWorld().isClient)
        {
            ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

            PlayerInventory inventory = player.getInventory();

            for (int i = 0; i < inventory.offHand.size(); i++)
            {
                tickTorch(inventory.offHand.get(i), inventory, i, inventory.offHand);
            }

            for (int i = 0; i < inventory.main.size(); i++)
            {
                tickTorch(inventory.main.get(i), inventory, i, inventory.main);
            }

            waterCheck(player, inventory);
        }
    }


    @Unique
    private void waterCheck(ServerPlayerEntity player, PlayerInventory inventory)
    {
        BlockPos pos = player.getBlockPos();
        boolean isRaining =  player.getWorld().hasRain(pos);

        for (int i = 0; i < inventory.size(); i++)
        {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();

            // Torches
            if (item instanceof TorchItem torchItem)
            {
                boolean mainOrOffhand = (i == inventory.selectedSlot || inventory.offHand.get(0) == stack);

                // Rain
                rainTorch(i, torchItem, stack, inventory, player.getWorld(), pos);

                // Underwater
                waterTorch(i, torchItem, stack, player, mainOrOffhand, pos);
            }
        }
    }


    @Unique
    private void waterTorch(int i, TorchItem torchItem, ItemStack stack, ServerPlayerEntity player, boolean mainOrOffhand, BlockPos pos)
    {
        if (player.isSubmergedInWater())
        {
            if (torchItem.getTorchState() == TorchFireState.LIT || torchItem.getTorchState() == TorchFireState.SMOULDER)
            {
                if ( mainOrOffhand )
                {
                    player.getInventory().setStack(i, TorchItem.stateStack(stack, TorchFireState.UNLIT));
                    player.getWorld().playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
                }
            }
        }
    }

    @Unique
    private void rainTorch(int i, TorchItem torchItem, ItemStack stack, PlayerInventory inventory, World world, BlockPos pos)
    {
        if (torchItem.getTorchState() == TorchFireState.LIT)
        {
            inventory.setStack(i, TorchItem.stateStack(stack, TorchFireState.SMOULDER));
            world.playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);

        }
        else if (torchItem.getTorchState() == TorchFireState.SMOULDER)
        {
            inventory.setStack(i, TorchItem.stateStack(stack, TorchFireState.BURNED_OUT));
            world.playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);

        }
    }

    @Unique
    private void tickTorch(ItemStack stack, PlayerInventory inventory, int index, DefaultedList<ItemStack> list)
    {
        Item item = stack.getItem();

        if (item instanceof TorchItem)
        {
            TorchFireState state = ((TorchItem) item).getTorchState();

            if (state == TorchFireState.LIT)
            {
                list.set(index, TorchItem.addFuel(stack, getWorld(),-1));
            }
            else if (state == TorchFireState.SMOULDER)
            {
                if (random.nextInt(3) == 0) list.set(index, TorchItem.addFuel(stack, getWorld(),-1));
            }
        }
    }
     **/

}