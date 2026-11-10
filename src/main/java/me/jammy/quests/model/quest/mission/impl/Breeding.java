package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Breeding extends Mission {
    public Breeding() {
        super(Quests.BREEDING, "<gold>Breeding", Material.WHEAT);
    }
}
