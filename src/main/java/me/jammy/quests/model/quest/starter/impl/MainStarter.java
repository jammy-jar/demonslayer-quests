package me.jammy.quests.model.quest.starter.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.starter.Starter;
import org.bukkit.Material;

public class MainStarter extends Starter {
    public MainStarter() {
        super(Quests.MAIN_STARTER, "<green>Starter", Material.EMERALD);
    }

    @Override
    public int getNpc() {
        return plugin.getStarterRequirementsCfg().get().getInt("urokodaki-npc-id");
    }
}
