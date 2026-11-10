package me.jammy.quests.model.miniboss;

import lombok.Getter;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.boss.Bosses;

public class MiniBoss extends Boss {
    @Getter
    private final MiniBosses miniboss;
    @Getter
    private final Boss parent;
    public MiniBoss(final Bosses boss, final MiniBosses miniboss, final Boss parent, final String displayName) {
        super(boss, displayName, parent.getLocation(), parent.getOpponent());
        this.miniboss = miniboss;
        this.parent = parent;
    }
}
