package me.jammy.quests.model.quest.starter;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import org.bukkit.Material;

public abstract class Starter extends Quest {
    protected final OasisQuests plugin = OasisQuests.instance();

    public Starter(final Quests quest, final String displayName, final Material icon) {
        super(QuestType.STARTER, quest, displayName, icon);
    }

    public abstract int getNpc();
}
