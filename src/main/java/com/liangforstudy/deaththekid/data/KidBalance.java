package com.liangforstudy.deaththekid.data;

/** Balance numbers from the design doc. These move to a server config in Phase L. */
public final class KidBalance {
    public static final int BASE_MAX_MANA = 100;
    public static final int MANA_HARD_CAP = 1000;

    public static final int MANA_REGEN_PER_SECOND = 3;
    public static final int MANA_REGEN_S_RANK_BONUS = 1;

    public static final float SOUL_DROP_CHANCE = 0.35f;
    public static final float SOUL_DROP_CHANCE_FULL_MOON = 1.0f;
    public static final int SOUL_DROP_COUNT = 1;
    public static final int SOUL_DROP_COUNT_FULL_MOON = 2;

    /** Mobs with at least this much max health count as elite even without the tag. */
    public static final float ELITE_HEALTH_THRESHOLD = 40.0f;

    public static final float PISTOL_MAX_HEALTH = 100.0f;
    public static final float PISTOL_WARNING_THRESHOLD = 30.0f;

    public static final int MAX_SANZU_LINES = 3;
    public static final int MISSION_COUNT = 4;

    private KidBalance() {}
}
