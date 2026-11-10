package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillPlayersInWarzone extends Daily {
    public KillPlayersInWarzone() {
        super(Quests.KILL_PLAYERS_IN_WARZONE, "<yellow>Kill Players in the Warzone", Material.NETHERITE_SWORD);
    }
}
