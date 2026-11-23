package com.liangforstudy.deaththekid.network;

import com.liangforstudy.deaththekid.DeathTheKid;
import com.liangforstudy.deaththekid.data.KidCapabilities;
import com.liangforstudy.deaththekid.data.KidData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Single network channel for all client-to-server input (shots, skills, keybinds)
 * and server-to-client sync (mana, rank, pistol health).
 */
public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            DeathTheKid.id("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextId = 0;

    public static void register() {
        CHANNEL.messageBuilder(SyncKidDataPacket.class, nextId++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncKidDataPacket::encode)
                .decoder(SyncKidDataPacket::decode)
                .consumerMainThread(SyncKidDataPacket::handle)
                .add();
    }

    /** Sends the player's full KidData to their client and clears the dirty flag. */
    public static void syncKidData(ServerPlayer player) {
        KidCapabilities.get(player).ifPresent(data -> syncKidData(player, data));
    }

    public static void syncKidData(ServerPlayer player, KidData data) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncKidDataPacket(data.save()));
        data.clearDirty();
    }

    private ModNetwork() {}
}
