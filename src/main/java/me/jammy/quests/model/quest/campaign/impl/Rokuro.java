package me.jammy.quests.model.quest.campaign.impl;

import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.campaign.Campaign;
import org.bukkit.Material;

public class Rokuro extends Campaign {
    public Rokuro() {
        super(QuestType.CAMPAIGN, Bosses.ROKURO, Material.VINE);
        // TODO: Change placeholder icon.
    }
}
