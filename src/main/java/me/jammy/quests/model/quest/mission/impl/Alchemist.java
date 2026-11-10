package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Alchemist extends Mission {

    public Alchemist() {
        super(Quests.ALCHEMIST, "<dark_purple>Alchemist", Material.POTION);
    }
}
