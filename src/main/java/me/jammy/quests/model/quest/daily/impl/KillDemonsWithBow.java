package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillDemonsWithBow extends Daily {
    public KillDemonsWithBow() {
        super(Quests.KILL_DEMONS_WITH_BOW, "<yellow>Kill Demons with Bow", Material.BOW);
    }
}
