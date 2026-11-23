package com.liangforstudy.deaththekid.registry;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeathTheKid.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + DeathTheKid.MOD_ID))
                    .icon(() -> ModItems.SHINIGAMI_PACT.get().getDefaultInstance())
                    .displayItems((params, output) -> ModItems.ITEMS.getEntries()
                            .forEach(item -> output.accept(item.get())))
                    .build());

    private ModCreativeTabs() {}
}
