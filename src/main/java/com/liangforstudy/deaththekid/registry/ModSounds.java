package com.liangforstudy.deaththekid.registry;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, DeathTheKid.MOD_ID);

    // Voice lines and SFX (gunshots, headshot bell, Beelzebub) are added in Phase K.

    private ModSounds() {}
}
