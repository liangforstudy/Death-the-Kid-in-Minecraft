package com.liangforstudy.deaththekid.data;

/** Style combo rank, lowest to highest. Combo logic arrives in Phase E. */
public enum StyleRank {
    D, C, B, A, S;

    public static StyleRank byOrdinal(int ordinal) {
        StyleRank[] values = values();
        return values[Math.max(0, Math.min(values.length - 1, ordinal))];
    }

    public boolean atLeast(StyleRank other) {
        return ordinal() >= other.ordinal();
    }
}
