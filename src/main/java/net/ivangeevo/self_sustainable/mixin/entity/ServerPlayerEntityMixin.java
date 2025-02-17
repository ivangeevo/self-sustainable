package net.ivangeevo.self_sustainable.mixin.entity;

import com.mojang.authlib.GameProfile;
import net.ivangeevo.self_sustainable.block.utils.TorchFireState;
import net.ivangeevo.self_sustainable.item.items.CrudeTorchItem;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique private static Random random = new Random();

    @Inject(at = @At("TAIL"), method = "tick")
    private void tick(CallbackInfo info) {
        if (!this.getWorld().isClient) {
            ServerPlayerEntity player = ((ServerPlayerEntity) (Object) this);

            PlayerInventory inventory = player.getInventory();

            for (int i = 0; i < inventory.offHand.size(); i++) {
                tickTorch(inventory.offHand.get(i), i, inventory.offHand);
            }

            for (int i = 0; i < inventory.main.size(); i++) {
                tickTorch(inventory.main.get(i), i, inventory.main);
            }

            waterCheck(player, inventory);
        }
    }


    @Unique
    private void waterCheck(ServerPlayerEntity player, PlayerInventory inventory) {
        BlockPos pos = player.getBlockPos();
        boolean isRainingOnTorch =  player.getWorld().hasRain(pos);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();

            // Torches
            if (item instanceof CrudeTorchItem torchItem) {
                boolean mainOrOffhand = (i == inventory.selectedSlot || inventory.offHand.getFirst() == stack);

                // Rain
                if (isRainingOnTorch && random.nextInt(200) == 0) {
                    rainTorch(torchItem, stack, player.getWorld(), pos);
                }

                // Underwater
                waterTorch(torchItem, stack, player, mainOrOffhand, pos);
            }
        }
    }


    @Unique
    private void waterTorch(CrudeTorchItem torchItem, ItemStack stack, ServerPlayerEntity player, boolean mainOrOffhand, BlockPos pos)
    {
        if (player.isSubmergedInWater()) {
            if (torchItem.getTorchState() == TorchFireState.LIT || torchItem.getTorchState() == TorchFireState.SMOULDER)
            {
                if ( mainOrOffhand ) {
                    stack.decrement(1);
                    player.getWorld().playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
                }
            }
        }
    }

    @Unique
    private void rainTorch(CrudeTorchItem torchItem, ItemStack stack, World world, BlockPos pos) {
        if (torchItem.getTorchState() == TorchFireState.LIT) {
            stack.decrement(1);
            world.playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
        }
    }

    @Unique
    private void tickTorch(ItemStack stack, int index, DefaultedList<ItemStack> list) {
        Item item = stack.getItem();

        if (item instanceof CrudeTorchItem) {
            TorchFireState state = ((CrudeTorchItem) item).getTorchState();

            if (state == TorchFireState.LIT) {
                list.set(index, CrudeTorchItem.addFuel(stack, getWorld(),-1));
            } else if (state == TorchFireState.SMOULDER) {
                if (random.nextInt(3) == 0) list.set(index, CrudeTorchItem.addFuel(stack, getWorld(),-1));
            }
        }
    }


}