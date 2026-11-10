package me.jammy.quests.model.miniboss;

import lombok.Getter;
import me.jammy.quests.model.boss.Bosses;

import java.util.Locale;

public enum MiniBosses {
    DAKI("<dark_red>Daki", Bosses.GYUTARO, Bosses.DAKI);

    @Getter
    private final String displayName;
    @Getter
    private final Bosses parent;
    @Getter
    private final Bosses bossType;

    MiniBosses(final String displayName, final Bosses parent, final Bosses bossType) {
        this.displayName = displayName;
        this.parent = parent;
        this.bossType = bossType;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static MiniBosses getEnum(String string) {
        for (MiniBosses value : values()) {
            if (value.toString().replace("_", "-").equalsIgnoreCase(string.replace("_", "-")))
                return value;
        }
        return null;
    }
}
