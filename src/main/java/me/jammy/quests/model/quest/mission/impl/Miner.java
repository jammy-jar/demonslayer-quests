package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Miner extends Mission {
    public Miner() {
        super(Quests.MINER, "<dark_gray>Miner", Material.STONE_PICKAXE);
    }
}
