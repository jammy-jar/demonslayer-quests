package me.jammy.quests.model.quest;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.user.User;
import org.bukkit.Material;

public abstract class Quest {
    private final Quests quest;
    private final String displayName;
    private final Material iconMaterial;
    private final QuestType questType;

    public Quest(QuestType questType, Quests quest, String displayName, Material icon) {
        this.questType = questType;
        this.quest = quest;
        this.displayName = displayName;
        this.iconMaterial = icon;
    }

    public Quests getQuest() {
        return quest;
    }

    public QuestType getQuestType() {
        return questType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return iconMaterial;
    }

    public void checkComplete(User user) {
        if (RequirementType.of(quest).stream().allMatch(req -> req.checkComplete(user)))
            OasisQuests.instance().getQuestManager().complete(user, this);
    }

    public boolean isCompleted(User user) {
        return user.getProgress(getQuest()).get(0).getAmount() == -1;
    }

    @Override
    public String toString() {
        return getQuest().toString();
    }
}
