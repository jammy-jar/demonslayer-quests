package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Playtime extends Mission {
    public Playtime() {
        super(Quests.PLAYTIME, "<blue>Playtime", Material.CLOCK);
    }
}
