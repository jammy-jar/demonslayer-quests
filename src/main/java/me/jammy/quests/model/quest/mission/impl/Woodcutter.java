package me.jammy.quests.model.quest.mission.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import org.bukkit.Material;

public class Woodcutter extends Mission {
    public Woodcutter() {
        super(Quests.WOODCUTTER, "<dark_green>Woodcutter", Material.STONE_AXE);
    }
}
