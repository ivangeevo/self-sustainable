package ivangeevo.selfsustainable.block.entity;

import ivangeevo.selfsustainable.client.gui.screen.OvenScreenHandler;
import ivangeevo.selfsustainable.entity.ModEntities;
import ivangeevo.selfsustainable.networking.NetworkMessagesRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class BrickOvenBlockEntity extends AbstractOvenBlockEntity  {
    public BrickOvenBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.Blocks.OVEN_BRICK, pos, state, RecipeType.SMELTING);
    }

    protected Text getContainerName() {
        return Text.translatable("container.oven_brick");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new OvenScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }




    public ItemStack getRenderStack() {
        ItemStack resultStack = this.getStack(2);
        ItemStack cookingStack = this.getStack(0);

        return resultStack.isEmpty() ? cookingStack : resultStack;
    }

    public void setInventory(DefaultedList<ItemStack> inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            this.inventory.set(i, inventory.get(i));
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();

        // Notify clients about the change in inventory
        if (world != null && !world.isClient()) {
            sync();
        }
    }

    private void sync() {
        PacketByteBuf data = PacketByteBufs.create();
        data.writeInt(inventory.size());
        for (ItemStack stack : inventory) {
            data.writeItemStack(stack);
        }
        data.writeBlockPos(getPos());

        assert world != null;
        PlayerLookup.tracking((ServerWorld) world, getPos())
                .forEach(player -> ServerPlayNetworking.send(player, NetworkMessagesRegistry.ITEM_SYNC, data));
    }
}
