package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Tamer extends Mission {
    public Tamer() {
        super(Quests.TAMER, "<yellow>Tamer", Material.BONE);
    }
}
