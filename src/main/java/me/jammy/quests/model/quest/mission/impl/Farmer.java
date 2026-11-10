package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Farmer extends Mission {
    public Farmer() {
        super(Quests.FARMER, "<dark_green>Farmer", Material.STONE_HOE);
    }
}
