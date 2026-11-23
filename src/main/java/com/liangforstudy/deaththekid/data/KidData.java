package com.liangforstudy.deaththekid.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

/**
 * Everything the mod tracks per player. Lives on the player as a Forge capability,
 * is saved with the player, survives death, and is synced to the owning client.
 */
public class KidData {
    // Soul Wavelength
    private float mana = KidBalance.BASE_MAX_MANA;
    private int maxMana = KidBalance.BASE_MAX_MANA;
    private int soulsAbsorbed;

    // Progression
    private int sanzuLines;
    private boolean contracted;

    // Combat state
    private boolean meisterMode;
    private StyleRank rank = StyleRank.D;
    private int combo;
    /** -100 = all weight on the left, +100 = all on the right, 0 = perfect symmetry. */
    private float symmetry;

    // Liz (right hand) and Patty (left hand)
    private float lizHealth = KidBalance.PISTOL_MAX_HEALTH;
    private float pattyHealth = KidBalance.PISTOL_MAX_HEALTH;

    // Shibusen missions and boss clues
    private final int[] missionProgress = new int[KidBalance.MISSION_COUNT];
    private int missionsAccepted; // bitmask
    private int missionsCompleted; // bitmask
    private int cluesFound; // bitmask

    private boolean dirty = true;

    // ---- Mana ----

    public float getMana() { return mana; }

    public void setMana(float value) {
        float clamped = Mth.clamp(value, 0, maxMana);
        if (clamped != mana) {
            mana = clamped;
            markDirty();
        }
    }

    public void addMana(float amount) { setMana(mana + amount); }

    /** Spends mana if there is enough. Returns false and spends nothing otherwise. */
    public boolean trySpendMana(float amount) {
        if (mana < amount) return false;
        setMana(mana - amount);
        return true;
    }

    public int getMaxMana() { return maxMana; }

    public void setMaxMana(int value) {
        int clamped = Mth.clamp(value, KidBalance.BASE_MAX_MANA, KidBalance.MANA_HARD_CAP);
        if (clamped != maxMana) {
            maxMana = clamped;
            mana = Math.min(mana, maxMana);
            markDirty();
        }
    }

    /** Raises max mana up to the hard cap. Returns how much was actually added. */
    public int addMaxMana(int amount) {
        int before = maxMana;
        setMaxMana(maxMana + amount);
        return maxMana - before;
    }

    public boolean isAtManaCap() { return maxMana >= KidBalance.MANA_HARD_CAP; }

    public int getSoulsAbsorbed() { return soulsAbsorbed; }

    public void addSoulsAbsorbed(int count) {
        soulsAbsorbed += count;
        markDirty();
    }

    // ---- Progression ----

    public int getSanzuLines() { return sanzuLines; }

    public void setSanzuLines(int lines) {
        int clamped = Mth.clamp(lines, 0, KidBalance.MAX_SANZU_LINES);
        if (clamped != sanzuLines) {
            sanzuLines = clamped;
            markDirty();
        }
    }

    public boolean isContracted() { return contracted; }

    public void setContracted(boolean value) {
        if (value != contracted) {
            contracted = value;
            markDirty();
        }
    }

    // ---- Combat ----

    public boolean isMeisterMode() { return meisterMode; }

    public void setMeisterMode(boolean value) {
        if (value != meisterMode) {
            meisterMode = value;
            markDirty();
        }
    }

    public StyleRank getRank() { return rank; }

    public void setRank(StyleRank value) {
        if (value != rank) {
            rank = value;
            markDirty();
        }
    }

    public int getCombo() { return combo; }

    public void setCombo(int value) {
        int clamped = Math.max(0, value);
        if (clamped != combo) {
            combo = clamped;
            markDirty();
        }
    }

    public void resetCombo() {
        setCombo(0);
        setRank(StyleRank.D);
    }

    public float getSymmetry() { return symmetry; }

    public void setSymmetry(float value) {
        float clamped = Mth.clamp(value, -100, 100);
        if (clamped != symmetry) {
            symmetry = clamped;
            markDirty();
        }
    }

    // ---- Pistols ----

    public float getLizHealth() { return lizHealth; }

    public void setLizHealth(float value) {
        float clamped = Mth.clamp(value, 0, KidBalance.PISTOL_MAX_HEALTH);
        if (clamped != lizHealth) {
            lizHealth = clamped;
            markDirty();
        }
    }

    public float getPattyHealth() { return pattyHealth; }

    public void setPattyHealth(float value) {
        float clamped = Mth.clamp(value, 0, KidBalance.PISTOL_MAX_HEALTH);
        if (clamped != pattyHealth) {
            pattyHealth = clamped;
            markDirty();
        }
    }

    /** Blocking and parrying wear both pistols evenly. */
    public void wearPistolsSymmetrically(float totalWear) {
        setLizHealth(lizHealth - totalWear / 2f);
        setPattyHealth(pattyHealth - totalWear / 2f);
    }

    public boolean isLizLow() { return lizHealth < KidBalance.PISTOL_WARNING_THRESHOLD; }

    public boolean isPattyLow() { return pattyHealth < KidBalance.PISTOL_WARNING_THRESHOLD; }

    // ---- Missions and clues ----

    public int getMissionProgress(int mission) { return missionProgress[mission]; }

    public void setMissionProgress(int mission, int value) {
        int clamped = Math.max(0, value);
        if (missionProgress[mission] != clamped) {
            missionProgress[mission] = clamped;
            markDirty();
        }
    }

    public boolean isMissionAccepted(int mission) { return (missionsAccepted & (1 << mission)) != 0; }

    public void setMissionAccepted(int mission, boolean value) {
        int updated = value ? missionsAccepted | (1 << mission) : missionsAccepted & ~(1 << mission);
        if (updated != missionsAccepted) {
            missionsAccepted = updated;
            markDirty();
        }
    }

    public boolean isMissionCompleted(int mission) { return (missionsCompleted & (1 << mission)) != 0; }

    public void setMissionCompleted(int mission, boolean value) {
        int updated = value ? missionsCompleted | (1 << mission) : missionsCompleted & ~(1 << mission);
        if (updated != missionsCompleted) {
            missionsCompleted = updated;
            markDirty();
        }
    }

    public boolean hasClue(int clue) { return (cluesFound & (1 << clue)) != 0; }

    public void addClue(int clue) {
        int updated = cluesFound | (1 << clue);
        if (updated != cluesFound) {
            cluesFound = updated;
            markDirty();
        }
    }

    // ---- Sync bookkeeping ----

    public boolean isDirty() { return dirty; }

    public void markDirty() { dirty = true; }

    public void clearDirty() { dirty = false; }

    // ---- Persistence ----

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Mana", mana);
        tag.putInt("MaxMana", maxMana);
        tag.putInt("SoulsAbsorbed", soulsAbsorbed);
        tag.putInt("SanzuLines", sanzuLines);
        tag.putBoolean("Contracted", contracted);
        tag.putBoolean("MeisterMode", meisterMode);
        tag.putInt("Rank", rank.ordinal());
        tag.putInt("Combo", combo);
        tag.putFloat("Symmetry", symmetry);
        tag.putFloat("LizHealth", lizHealth);
        tag.putFloat("PattyHealth", pattyHealth);
        tag.putIntArray("MissionProgress", missionProgress.clone());
        tag.putInt("MissionsAccepted", missionsAccepted);
        tag.putInt("MissionsCompleted", missionsCompleted);
        tag.putInt("CluesFound", cluesFound);
        return tag;
    }

    public void load(CompoundTag tag) {
        maxMana = tag.contains("MaxMana")
                ? Mth.clamp(tag.getInt("MaxMana"), KidBalance.BASE_MAX_MANA, KidBalance.MANA_HARD_CAP)
                : KidBalance.BASE_MAX_MANA;
        mana = tag.contains("Mana") ? Mth.clamp(tag.getFloat("Mana"), 0, maxMana) : maxMana;
        soulsAbsorbed = tag.getInt("SoulsAbsorbed");
        sanzuLines = Mth.clamp(tag.getInt("SanzuLines"), 0, KidBalance.MAX_SANZU_LINES);
        contracted = tag.getBoolean("Contracted");
        meisterMode = tag.getBoolean("MeisterMode");
        rank = StyleRank.byOrdinal(tag.getInt("Rank"));
        combo = tag.getInt("Combo");
        symmetry = tag.getFloat("Symmetry");
        lizHealth = tag.contains("LizHealth") ? tag.getFloat("LizHealth") : KidBalance.PISTOL_MAX_HEALTH;
        pattyHealth = tag.contains("PattyHealth") ? tag.getFloat("PattyHealth") : KidBalance.PISTOL_MAX_HEALTH;
        int[] savedProgress = tag.getIntArray("MissionProgress");
        for (int i = 0; i < missionProgress.length; i++) {
            missionProgress[i] = i < savedProgress.length ? savedProgress[i] : 0;
        }
        missionsAccepted = tag.getInt("MissionsAccepted");
        missionsCompleted = tag.getInt("MissionsCompleted");
        cluesFound = tag.getInt("CluesFound");
        markDirty();
    }

    /**
     * Copies data onto the new player entity after death or returning from the End.
     * Permanent progress always carries over. After a death, combat state resets and
     * mana refills.
     */
    public void copyFrom(KidData other, boolean wasDeath) {
        load(other.save());
        if (wasDeath) {
            meisterMode = false;
            rank = StyleRank.D;
            combo = 0;
            symmetry = 0;
            mana = maxMana;
            // "Intact Symmetry" (16 kills without taking damage) cannot survive a death.
            missionProgress[0] = 0;
        }
        markDirty();
    }
}
