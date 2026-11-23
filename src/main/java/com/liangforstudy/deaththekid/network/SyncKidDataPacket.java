package com.liangforstudy.deaththekid.network;

import com.liangforstudy.deaththekid.client.ClientPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server to client: the full KidData of the receiving player. */
public record SyncKidDataPacket(CompoundTag data) {

    public static void encode(SyncKidDataPacket packet, FriendlyByteBuf buf) {
        buf.writeNbt(packet.data);
    }

    public static SyncKidDataPacket decode(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        return new SyncKidDataPacket(tag != null ? tag : new CompoundTag());
    }

    public static void handle(SyncKidDataPacket packet, Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.handleKidDataSync(packet.data));
    }
}
