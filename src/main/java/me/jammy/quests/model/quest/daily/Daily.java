package me.jammy.quests.model.quest.daily;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import org.bukkit.Material;

public abstract class Daily extends Quest {

    private final OasisQuests plugin = OasisQuests.instance();

    public Daily(final Quests quest, final String displayName, final Material icon) {
        super(QuestType.DAILY, quest, displayName, icon);

        // TODO: Add interact with NPC quest.
        // TODO: Add advanced enchant events.
        // TODO: Add Change Breath & Kill 50 Demons Quest.
        // TODO: Add mid and high tiers.
        // TODO: Rework dailys to have multiple requirements.
    }

    public boolean isQuestRunning() {
        return plugin.getQuestManager().todaysDailys().stream().anyMatch(daily -> daily.getQuest() == getQuest());
    }
}
