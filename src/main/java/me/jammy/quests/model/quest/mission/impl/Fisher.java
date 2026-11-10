package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Fisher extends Mission {
    public Fisher() {
        super(Quests.FISHER, "<aqua>Fisher", Material.FISHING_ROD);
    }
}
