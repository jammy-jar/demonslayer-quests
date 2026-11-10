package me.jammy.quests.model.quest.campaign.impl;

import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.campaign.Campaign;
import org.bukkit.Material;

public class Rui extends Campaign {
    public Rui() {
        super(QuestType.CAMPAIGN, Bosses.RUI, Material.VINE);
        // TODO: Change placeholder icon.
    }
}
