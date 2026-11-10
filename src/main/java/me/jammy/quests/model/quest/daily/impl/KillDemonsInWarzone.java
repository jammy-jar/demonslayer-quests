package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class KillDemonsInWarzone extends Daily {
    public KillDemonsInWarzone() {
        super(Quests.KILL_DEMONS_IN_WARZONE, "<yellow>Kill Demons in the Warzone", Material.NETHERITE_SWORD);
    }
}
