package com.liangforstudy.deaththekid.data;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = DeathTheKid.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KidCapabilities {
    public static final Capability<KidData> KID_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(KidData.class);
    }

    /** The player's Kid data, or empty if the capability is missing (e.g. a removed entity). */
    public static Optional<KidData> get(Player player) {
        return player.getCapability(KID_DATA).resolve();
    }

    private KidCapabilities() {}
}
