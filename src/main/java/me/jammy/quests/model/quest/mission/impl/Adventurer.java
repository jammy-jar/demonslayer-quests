package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Adventurer extends Mission {
    public Adventurer() {
        super(Quests.ADVENTURER, "<yellow>Adventurer", Material.LEATHER_BOOTS);
    }
}
