package net.ivangeevo.selfsustainable.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.ivangeevo.selfsustainable.block.entity.BrickOvenBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ItemStackSyncS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {

       //syncBrickOvenCookStackSlot(client , buf);

    }

    /**
    private static void syncBrickOvenCookStackSlot (MinecraftClient client, PacketByteBuf buf)
    {
        int size = buf.readInt();
        DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);
        for(int i = 0; i < size; i++) {
            list.set(i, buf.readItemStack());
        }
        BlockPos position = buf.readBlockPos();

        assert client.world != null;

        if (client.world.getBlockEntity(position) instanceof BrickOvenBlockEntity blockEntity) {
            blockEntity.setCookStackToInv(list);
        }
    }
     **/
}