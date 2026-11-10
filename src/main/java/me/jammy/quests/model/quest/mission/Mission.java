package me.jammy.quests.model.quest.mission;

import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.requirement.RequirementParser;
import me.jammy.quests.model.requirement.RequirementType;
import org.bukkit.Material;

public abstract class Mission extends Quest {
    public Mission(Quests quest, String displayName, Material icon) {
        super(QuestType.MISSION, quest, displayName, icon);
    }

    public int getMaxLevel() {
        return RequirementParser.missionRequirements(RequirementType.getEnum(getQuest().toString())).size();
    }
}
