package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Experience extends Mission {
    public Experience() {
        super(Quests.EXPERIENCE, "<green>Experience", Material.EXPERIENCE_BOTTLE);
    }
}
