package me.jammy.quests.util;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.requirement.RequirementClassification;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestExpansion extends PlaceholderExpansion {
    private final OasisQuests plugin;
    private final QuestManager questManager;

    public QuestExpansion(OasisQuests plugin) {
        this.plugin = plugin;
        this.questManager = plugin.getQuestManager();
    }

    @Override
    public @NotNull String getAuthor() {
        return "jammy";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "quests";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (!player.isOnline())
            return null;
        User user = plugin.getUserManager().getUser((Player) player);

        String[] strings = params.split("_");

        if (strings.length < 3)
            return "";

        String questType = strings[1].substring(0, strings[1].length() - 1);
        List<Quests> quests = user.getActiveQuests().stream().filter(q -> q.getQuestType() == QuestType.getEnum(questType)).toList();

        int num;
        try {
            num = Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
            return "";
        }

        if (num > quests.size())
            return "";

        Quests quest = quests.get(num - 1);

        int i = 1;
        RequirementType requirement = RequirementType.of(quest).get(0);
        while (requirement.checkComplete(user)) {
            if (i > RequirementType.of(quest).size() - 1)
                return "";
            requirement = RequirementType.of(quest).get(i);
            i++;
        }

        return questToString(user, requirement);
    }

    private String questToString(User user, RequirementType requirement) {
        String string;
        if (requirement.getClassification() == RequirementClassification.QUANTIFIABLE)
            string = "<gray>- " + questManager.getQuest(requirement.getQuest()).getDisplayName() + "<yellow>    " + user.getProgress(requirement) + " / " + requirement.getAmount();
        else if (requirement.getClassification() == RequirementClassification.LEVELLABLE)
            string = "<gray>- " + questManager.getQuest(requirement.getQuest()).getDisplayName() + "<yellow>    " + user.getProgress(requirement) + " / " + requirement.getAmount(user.getLevel((Mission) questManager.getQuest(requirement.getQuest())));
        else
            string = "<gray>- " + questManager.getQuest(requirement.getQuest()).getDisplayName() + "    <red>INCOMPLETE";

        return Msgs.of(string).asLegacy();
    }
}
