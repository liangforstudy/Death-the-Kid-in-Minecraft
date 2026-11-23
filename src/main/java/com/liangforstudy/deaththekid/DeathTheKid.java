package com.liangforstudy.deaththekid;

import com.liangforstudy.deaththekid.network.ModNetwork;
import com.liangforstudy.deaththekid.registry.ModCreativeTabs;
import com.liangforstudy.deaththekid.registry.ModItems;
import com.liangforstudy.deaththekid.registry.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DeathTheKid.MOD_ID)
public class DeathTheKid {
    public static final String MOD_ID = "deaththekid";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DeathTheKid() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modBus);
        ModSounds.SOUNDS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);

        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetwork::register);
        LOGGER.info("Death the Kid loaded. Symmetry is order.");
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
