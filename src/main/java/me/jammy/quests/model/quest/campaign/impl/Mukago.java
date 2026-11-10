package me.jammy.quests.model.quest.campaign.impl;

import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.campaign.Campaign;
import org.bukkit.Material;

public class Mukago extends Campaign {
    public Mukago() {
        super(QuestType.CAMPAIGN, Bosses.MUKAGO, Material.VINE);
        // TODO: Change placeholder icon.
    }
}
