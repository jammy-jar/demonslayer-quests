package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class CollectXPFromOres extends Daily {
    public CollectXPFromOres() {
        super(Quests.COLLECT_XP_FROM_ORES, "<yellow>Collect XP from Ores", Material.EXPERIENCE_BOTTLE);
    }
}
