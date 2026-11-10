package me.jammy.quests.model.requirement;

import com.udojava.evalex.Expression;
import me.jammy.quests.OasisQuests;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RequirementParser {
    public static String description(RequirementType requirementType) {
        return switch (requirementType.getQuest().getQuestType()) {
            case MISSION ->
                    OasisQuests.instance().getMissionRequirementsCfg().get().getString(requirementType + ".description");
            case CAMPAIGN ->
                    OasisQuests.instance().getCampaignRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".description");
            case DAILY ->
                    OasisQuests.instance().getDailyRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".description");
            case SPECIAL ->
                    OasisQuests.instance().getSpecialRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".description");
            case STARTER ->
                    OasisQuests.instance().getStarterRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".description");
        };
    }

    public static String unit(RequirementType requirementType) {
        String unit = switch (requirementType.getQuest().getQuestType()) {
            case MISSION ->
                    OasisQuests.instance().getMissionRequirementsCfg().get().getString(requirementType + ".unit");
            case CAMPAIGN ->
                    OasisQuests.instance().getCampaignRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".unit");
            case DAILY ->
                    OasisQuests.instance().getDailyRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".unit");
            case SPECIAL ->
                    OasisQuests.instance().getSpecialRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".unit");
            case STARTER ->
                    OasisQuests.instance().getStarterRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".unit");

        };
        return unit == null ? "N/A" : unit;
    }

    public static int requirement(RequirementType requirementType) {
        return switch (requirementType.getQuest().getQuestType()) {
            case MISSION -> 0;
            case CAMPAIGN ->
                    OasisQuests.instance().getCampaignRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".required-amount");
            case DAILY ->
                    OasisQuests.instance().getDailyRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".required-amount");
            case SPECIAL ->
                    OasisQuests.instance().getSpecialRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".required-amount");
            case STARTER ->
                    OasisQuests.instance().getStarterRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".required-amount");

        };
    }

    public static int tier(RequirementType requirementType) {
        return switch (requirementType.getQuest().getQuestType()) {
            case CAMPAIGN -> OasisQuests.instance().getCampaignRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".tier");
            case SPECIAL -> OasisQuests.instance().getSpecialRequirementsCfg().get().getInt(requirementType.getQuest() + "." + requirementType + ".tier");
            default -> 0;
        };
    }

    public static String permission(RequirementType requirementType) {
        if (requirementType.getClassification() != RequirementClassification.PERMISSION)
            return null;
        return switch (requirementType.getQuest().getQuestType()) {
            case CAMPAIGN ->
                    OasisQuests.instance().getCampaignRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".permission");
            case STARTER ->
                    OasisQuests.instance().getStarterRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".permission");
            case SPECIAL ->
                    OasisQuests.instance().getSpecialRequirementsCfg().get().getString(requirementType.getQuest() + "." + requirementType + ".permission");
            default -> null;
        };
    }

    public static List<Integer> missionRequirements(RequirementType requirementType) {
        int maxLevel = OasisQuests.instance().getMissionRequirementsCfg().get().getInt("max-level");
        maxLevel = maxLevel == 0 ? 100 : maxLevel;

        ConfigurationSection section = OasisQuests.instance().getMissionRequirementsCfg().get().getConfigurationSection(requirementType.toString());
        List<Integer> requirementAmounts;
        if (section.getString("system").equalsIgnoreCase("custom"))
            requirementAmounts = customRequirements(requirementType, maxLevel);
        else
            requirementAmounts = expressionRequirements(requirementType, maxLevel);

        return requirementAmounts;
    }

    private static List<Integer> customRequirements(RequirementType requirementType, int maxLevel) {
        ConfigurationSection section = OasisQuests.instance().getMissionRequirementsCfg().get().getConfigurationSection(requirementType + ".custom-requirements");
        List<Integer> requirements = new ArrayList<>();
        List<String> keys = section.getKeys(false).stream().limit(maxLevel).toList();
        for (String key : keys) {
            int value = section.getInt(key);
            requirements.add(value);
        }
        return requirements;
    }

    private static List<Integer> expressionRequirements(RequirementType requirementType, int maxLevel) {
        String expressionString = OasisQuests.instance().getMissionRequirementsCfg().get().getString(requirementType + ".expression");
        List<Integer> requirements = new ArrayList<>();
        Expression expression = new Expression(expressionString);
        for (int i = 0; i < maxLevel; i++) {
            expression.setVariable("level", BigDecimal.valueOf(i + 1));
            requirements.add(expression.eval().intValue());
        }
        return requirements;
    }

}
