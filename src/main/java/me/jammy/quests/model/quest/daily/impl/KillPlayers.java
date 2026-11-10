package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillPlayers extends Daily {
    public KillPlayers() {
        super(Quests.KILL_PLAYERS, "<yellow>Kill Players", Material.IRON_SWORD);
    }
}
