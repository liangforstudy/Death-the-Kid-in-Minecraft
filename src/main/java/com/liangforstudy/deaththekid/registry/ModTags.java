package com.liangforstudy.deaththekid.registry;

import com.liangforstudy.deaththekid.DeathTheKid;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public final class ModTags {
    /** Monsters that drop Corrupted Kishin Souls. Datapacks can add to this. */
    public static final TagKey<EntityType<?>> CORRUPTED_SOUL_MOBS =
            TagKey.create(Registries.ENTITY_TYPE, DeathTheKid.id("corrupted_soul_mobs"));

    /** Monsters that drop Elite Kishin Souls. Datapacks can add to this. */
    public static final TagKey<EntityType<?>> ELITE_SOUL_MOBS =
            TagKey.create(Registries.ENTITY_TYPE, DeathTheKid.id("elite_soul_mobs"));

    /** Monsters that drop Boss Kishin Souls, on top of Forge's forge:bosses tag. */
    public static final TagKey<EntityType<?>> BOSS_SOUL_MOBS =
            TagKey.create(Registries.ENTITY_TYPE, DeathTheKid.id("boss_soul_mobs"));

    private ModTags() {}
}
