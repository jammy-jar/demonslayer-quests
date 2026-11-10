package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillDemonsWithFist extends Daily {
    public KillDemonsWithFist() {
        super(Quests.KILL_DEMONS_WITH_FIST, "<yellow>Kill Demons with Fist", Material.CHAINMAIL_CHESTPLATE);
    }
}
