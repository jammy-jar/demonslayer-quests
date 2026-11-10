package me.jammy.quests.model.quest.campaign.impl;

import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.campaign.Campaign;
import org.bukkit.Material;

public class Wakurabo extends Campaign {
    public Wakurabo() {
        super(QuestType.CAMPAIGN, Bosses.WAKURABO, Material.VINE);
        // TODO: Change placeholder icon.
    }
}
