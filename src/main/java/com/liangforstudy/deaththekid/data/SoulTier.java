package com.liangforstudy.deaththekid.data;

/** Kishin Soul tiers and how much permanent max mana each one grants. */
public enum SoulTier {
    MINOR(1),
    CORRUPTED(3),
    ELITE(10),
    BOSS(50);

    private final int maxManaBonus;

    SoulTier(int maxManaBonus) {
        this.maxManaBonus = maxManaBonus;
    }

    public int maxManaBonus() {
        return maxManaBonus;
    }
}
