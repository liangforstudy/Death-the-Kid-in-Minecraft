package com.liangforstudy.deaththekid.item;

import com.liangforstudy.deaththekid.data.KidBalance;
import com.liangforstudy.deaththekid.data.KidCapabilities;
import com.liangforstudy.deaththekid.data.KidData;
import com.liangforstudy.deaththekid.data.SoulTier;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A Kishin Soul. Right-click absorbs one soul, sneak + right-click absorbs the whole
 * stack. Each soul permanently raises max mana by its tier's bonus, up to the hard cap.
 */
public class KishinSoulItem extends Item {
    private final SoulTier tier;

    public KishinSoulItem(SoulTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public SoulTier getTier() {
        return tier;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return tier == SoulTier.ELITE || tier == SoulTier.BOSS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        Optional<KidData> maybeData = KidCapabilities.get(player);
        if (maybeData.isEmpty()) return InteractionResultHolder.pass(stack);
        KidData data = maybeData.get();

        if (data.isAtManaCap()) {
            player.displayClientMessage(Component.translatable("message.deaththekid.soul.at_cap",
                    KidBalance.MANA_HARD_CAP), true);
            return InteractionResultHolder.fail(stack);
        }

        int wanted = player.isShiftKeyDown() ? stack.getCount() : 1;
        int absorbed = 0;
        int manaGained = 0;
        while (absorbed < wanted && !data.isAtManaCap()) {
            manaGained += data.addMaxMana(tier.maxManaBonus());
            absorbed++;
        }
        data.addSoulsAbsorbed(absorbed);
        if (!player.getAbilities().instabuild) stack.shrink(absorbed);

        level.playSound(null, player.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0f,
                0.8f + level.random.nextFloat() * 0.4f);
        player.displayClientMessage(Component.translatable("message.deaththekid.soul.absorbed",
                absorbed, manaGained, data.getMaxMana()), true);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.deaththekid.soul.bonus", tier.maxManaBonus()));
        tooltip.add(Component.translatable("tooltip.deaththekid.soul.usage"));
    }
}
