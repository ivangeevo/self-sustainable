package org.btwr.self_sustainable.network;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.btwr.shared_library.util.utils.IdUtils;

public record SyncWickerBasketS2C(BlockPos pos, DefaultedList<ItemStack> inventory) implements CustomPayload {
    public static final Identifier SYNC_BASKET_PAYLOAD_ID = IdUtils.ofSS("sync_basket");
    public static final CustomPayload.Id<SyncWickerBasketS2C> ID = new CustomPayload.Id<>(SYNC_BASKET_PAYLOAD_ID);

    public static final PacketCodec<RegistryByteBuf, DefaultedList<ItemStack>> ITEMSTACK_LIST_CODEC = new PacketCodec<>() {

        @Override
        public void encode(RegistryByteBuf buf, DefaultedList<ItemStack> value) {
            buf.writeVarInt(value.size());

            for (ItemStack stack : value) {
                ItemStack.OPTIONAL_PACKET_CODEC.encode(buf, stack);
            }
        }

        @Override
        public DefaultedList<ItemStack> decode(RegistryByteBuf buf) {
            int size = buf.readVarInt();
            DefaultedList<ItemStack> list = DefaultedList.ofSize(size, ItemStack.EMPTY);

            for (int i = 0; i < size; i++) {
                ItemStack stack = ItemStack.OPTIONAL_PACKET_CODEC.decode(buf);
                list.set(i, stack);
            }

            return list;
        }
    };

    public static final PacketCodec<RegistryByteBuf, SyncWickerBasketS2C> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, SyncWickerBasketS2C::pos,
            ITEMSTACK_LIST_CODEC, SyncWickerBasketS2C::inventory,
            SyncWickerBasketS2C::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
