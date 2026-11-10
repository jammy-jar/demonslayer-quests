package me.jammy.quests.model.quest.special;

import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import org.bukkit.Material;

public class Special extends Quest {
    public Special(final Quests quest, final String displayName, final Material icon) {
        super(QuestType.SPECIAL, quest, displayName, icon);
        // TODO: Add rest of specials.
        // TODO: Add completion for visiting villages.
    }
}
