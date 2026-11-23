package com.liangforstudy.deaththekid.event;

import com.liangforstudy.deaththekid.DeathTheKid;
import com.liangforstudy.deaththekid.data.KidBalance;
import com.liangforstudy.deaththekid.data.SoulTier;
import com.liangforstudy.deaththekid.registry.ModItems;
import com.liangforstudy.deaththekid.registry.ModTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathTheKid.MOD_ID)
public final class SoulDropEvents {

    /** Monsters killed by a player can drop a Kishin Soul: 35%, or 100% x2 on a full-moon night. */
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();
        if (level.isClientSide || !(entity instanceof Enemy)) return;
        if (!(event.getSource().getEntity() instanceof Player)) return;

        boolean nightOfMadness = isNightOfMadness(level);
        float chance = nightOfMadness ? KidBalance.SOUL_DROP_CHANCE_FULL_MOON : KidBalance.SOUL_DROP_CHANCE;
        if (level.random.nextFloat() >= chance) return;

        int count = nightOfMadness ? KidBalance.SOUL_DROP_COUNT_FULL_MOON : KidBalance.SOUL_DROP_COUNT;
        ItemStack souls = new ItemStack(ModItems.soulFor(tierOf(entity)), count);
        ItemEntity drop = new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(), souls);
        drop.setDefaultPickUpDelay();
        event.getDrops().add(drop);
    }

    /** The Night of Madness: night time during a full moon (moon phase 0). */
    public static boolean isNightOfMadness(Level level) {
        return level.dimensionType().hasSkyLight() && level.isNight() && level.getMoonPhase() == 0;
    }

    /**
     * Tags always win; the 40-max-health elite rule only applies to monsters on no list.
     * (Design call made by Claude, not from the original spec: an explicit tag is a deliberate
     * choice, so e.g. hoglins in corrupted_soul_mobs drop Corrupted souls despite 40 health.)
     */
    public static SoulTier tierOf(LivingEntity entity) {
        var type = entity.getType();
        if (type.is(Tags.EntityTypes.BOSSES) || type.is(ModTags.BOSS_SOUL_MOBS)) return SoulTier.BOSS;
        if (type.is(ModTags.ELITE_SOUL_MOBS)) return SoulTier.ELITE;
        if (type.is(ModTags.CORRUPTED_SOUL_MOBS)) return SoulTier.CORRUPTED;
        if (entity.getMaxHealth() >= KidBalance.ELITE_HEALTH_THRESHOLD) return SoulTier.ELITE;
        return SoulTier.MINOR;
    }

    private SoulDropEvents() {}
}
