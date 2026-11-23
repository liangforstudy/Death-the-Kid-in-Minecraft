package com.liangforstudy.deaththekid.client;

import com.liangforstudy.deaththekid.data.KidCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;

/** Client-only packet handling. Only referenced from inside DistExecutor calls. */
public final class ClientPacketHandler {

    public static void handleKidDataSync(CompoundTag tag) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        KidCapabilities.get(player).ifPresent(data -> {
            data.load(tag);
            data.clearDirty();
        });
    }

    private ClientPacketHandler() {}
}
