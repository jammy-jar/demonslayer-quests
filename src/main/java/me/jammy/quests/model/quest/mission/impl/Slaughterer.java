package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Slaughterer extends Mission {


    public Slaughterer() {
        super(Quests.SLAUGHTERER, "<red>Slaughterer", Material.STONE_SWORD);
    }
}
