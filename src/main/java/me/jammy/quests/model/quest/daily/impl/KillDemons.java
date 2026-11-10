package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillDemons extends Daily {
    public KillDemons() {
        super(Quests.KILL_DEMONS, "<yellow>Kill Demons", Material.NETHERITE_SWORD);
    }
}
