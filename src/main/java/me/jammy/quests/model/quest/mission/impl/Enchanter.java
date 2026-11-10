package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Enchanter extends Mission {

    public Enchanter() {
        super(Quests.ENCHANTER, "<light_purple>Enchanter", Material.ENCHANTED_BOOK);
    }
}
