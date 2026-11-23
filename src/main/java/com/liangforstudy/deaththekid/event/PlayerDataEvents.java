package com.liangforstudy.deaththekid.event;

import com.liangforstudy.deaththekid.DeathTheKid;
import com.liangforstudy.deaththekid.data.KidBalance;
import com.liangforstudy.deaththekid.data.KidCapabilities;
import com.liangforstudy.deaththekid.data.KidDataProvider;
import com.liangforstudy.deaththekid.data.StyleRank;
import com.liangforstudy.deaththekid.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTheKid.MOD_ID)
public final class PlayerDataEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            KidDataProvider provider = new KidDataProvider();
            event.addCapability(DeathTheKid.id("kid_data"), provider);
            event.addListener(provider::invalidate);
        }
    }

    /** Copies data to the new player entity on respawn or when returning from the End. */
    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        original.reviveCaps();
        KidCapabilities.get(original).ifPresent(oldData ->
                KidCapabilities.get(event.getEntity()).ifPresent(newData ->
                        newData.copyFrom(oldData, event.isWasDeath())));
        original.invalidateCaps();
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) ModNetwork.syncKidData(player);
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) ModNetwork.syncKidData(player);
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) ModNetwork.syncKidData(player);
    }

    /** Mana regeneration once per second, then a sync if anything changed this tick. */
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side != LogicalSide.SERVER) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        KidCapabilities.get(player).ifPresent(data -> {
            if (player.tickCount % 20 == 0 && player.isAlive()) {
                int regen = KidBalance.MANA_REGEN_PER_SECOND;
                if (data.getRank() == StyleRank.S) regen += KidBalance.MANA_REGEN_S_RANK_BONUS;
                data.addMana(regen);
            }
            if (data.isDirty()) ModNetwork.syncKidData(player, data);
        });
    }

    private PlayerDataEvents() {}
}
