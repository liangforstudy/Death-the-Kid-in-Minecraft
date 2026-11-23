package com.liangforstudy.deaththekid.network;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Single network channel for all client-to-server input (shots, skills, keybinds)
 * and server-to-client sync (mana, rank, pistol health). Packets are added here
 * from Phase B onward with CHANNEL.messageBuilder(...).
 */
public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            DeathTheKid.id("main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextId = 0;

    public static int nextId() {
        return nextId++;
    }

    public static void register() {
        // Packet registrations go here.
    }

    private ModNetwork() {}
}
