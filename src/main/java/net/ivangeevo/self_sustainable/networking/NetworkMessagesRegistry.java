package net.ivangeevo.self_sustainable.networking;

import net.ivangeevo.self_sustainable.SelfSustainableMod;
import net.ivangeevo.self_sustainable.networking.packet.ItemStackSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkMessagesRegistry {

    public static final Identifier ITEM_SYNC = new Identifier(SelfSustainableMod.MOD_ID, "item_sync");



    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(ITEM_SYNC, ItemStackSyncS2CPacket::receive);

    }

    public static void registerC2SPackets() {

    }
}
