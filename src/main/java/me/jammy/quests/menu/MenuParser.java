package me.jammy.quests.menu;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Bosses;

public class MenuParser {
    public static int fightCost(Bosses boss) {
        int fightCost = OasisQuests.instance().getBossCfg().get().getInt(boss + ".cost");
        // TODO: Fight cost / 4 if already attempted.
        return fightCost;
    }
}
