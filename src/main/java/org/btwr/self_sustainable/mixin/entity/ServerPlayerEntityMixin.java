package org.btwr.self_sustainable.mixin.entity;

import com.mojang.authlib.GameProfile;
import org.btwr.self_sustainable.block.utils.TorchFireState;
import org.btwr.self_sustainable.item.ModItems;
import org.btwr.self_sustainable.item.items.CrudeTorchBlockItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.VerticallyAttachableBlockItem;
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

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    // Tick torches in the inventory
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

            checkWaterBehavior(player, inventory);
        }
    }

    @Unique
    private void checkWaterBehavior(ServerPlayerEntity player, PlayerInventory inventory) {
        // Do not apply water behavior when player is creative or spectator
        if (player.isCreative() || player.isSpectator()) return;

        BlockPos pos = player.getBlockPos();
        boolean isRainingOnTorch =  player.getWorld().hasRain(pos);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            Item item = stack.getItem();

            // Crude torches
            if (item instanceof CrudeTorchBlockItem torchItem) {

                // Rain
                if (isRainingOnTorch && random.nextInt(200) == 0) {
                    rainTorch(torchItem, stack, player.getWorld(), pos);
                }

                destroyInWater(torchItem, stack, player, pos);
            }

            // Regular torches
            if (item instanceof VerticallyAttachableBlockItem) {
                extinguishInWater(stack, player, pos, i);
            }
        }
    }

    @Unique
    private void destroyInWater(CrudeTorchBlockItem torchItem, ItemStack stack, ServerPlayerEntity player, BlockPos pos) {
        if (player.isSubmergedInWater()) {
            if (isBurning(torchItem)) {
                stack.decrement(1);
                player.getWorld().playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
            }
        }
    }

    @Unique
    private void extinguishInWater(ItemStack stack, PlayerEntity player, BlockPos pos, int slot) {
        if (player.isSubmergedInWater() && stack.isOf(Items.TORCH)) {
            ItemStack unlitTorch = new ItemStack(ModItems.TORCH_UNLIT, stack.getCount());
            player.getInventory().setStack(slot, unlitTorch);
            player.getWorld().playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
        }
    }

    @Unique
    private void rainTorch(CrudeTorchBlockItem torchItem, ItemStack stack, World world, BlockPos pos) {
        if (isBurning(torchItem)) {
            stack.decrement(1);
            world.playSound(null, pos.up(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 0.5f, 1f);
        }
    }

    /** Lit or Smouldering **/
    @Unique
    private boolean isBurning(CrudeTorchBlockItem torchItem) {
        return torchItem.getTorchState() == TorchFireState.LIT || torchItem.getTorchState() == TorchFireState.SMOULDER;
    }

    @Unique
    private void tickTorch(ItemStack stack, int index, DefaultedList<ItemStack> list) {
        Item item = stack.getItem();

        if (item instanceof CrudeTorchBlockItem) {
            TorchFireState state = ((CrudeTorchBlockItem) item).getTorchState();

            if (state == TorchFireState.LIT) {
                list.set(index, CrudeTorchBlockItem.addFuel(stack, getWorld(),-1));
            } else if (state == TorchFireState.SMOULDER) {
                if (random.nextInt(3) == 0) list.set(index, CrudeTorchBlockItem.addFuel(stack, getWorld(),-1));
            }
        }
    }

}