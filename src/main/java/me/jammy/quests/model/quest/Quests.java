package me.jammy.quests.model.quest;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum Quests {
    ADVENTURER(QuestType.MISSION),
    ALCHEMIST(QuestType.MISSION),
    BREEDING(QuestType.MISSION),
    ENCHANTER(QuestType.MISSION),
    EXPERIENCE(QuestType.MISSION),
    FARMER(QuestType.MISSION),
    FISHER(QuestType.MISSION),
    MINER(QuestType.MISSION),
    PLAYTIME(QuestType.MISSION),
    SLAUGHTERER(QuestType.MISSION),
    TAMER(QuestType.MISSION),
    WOODCUTTER(QuestType.MISSION),

    KILL_PLAYERS(QuestType.DAILY),
    KILL_DEMONS(QuestType.DAILY),
    KILL_DEMONS_WITH_FIST(QuestType.DAILY),
    KILL_DEMONS_WITH_BOW(QuestType.DAILY),
    KILL_DEMONS_IN_WARZONE(QuestType.DAILY),
    COLLECT_XP_FROM_ORES(QuestType.DAILY),
    KILL_PLAYERS_IN_WARZONE(QuestType.DAILY),
    SWIM(QuestType.DAILY),
    FISH_FISH(QuestType.DAILY),
    BREED_CHICKENS(QuestType.DAILY),
    BREW(QuestType.DAILY),

    SEASON_3(QuestType.SPECIAL),
    ADVENTURER_MASTER(QuestType.SPECIAL),
    SLAYER(QuestType.SPECIAL),
    AUGUST_SPECIAL(QuestType.SPECIAL),

    MAIN_STARTER(QuestType.STARTER),

    KYOGAI(QuestType.CAMPAIGN),
    RUI(QuestType.CAMPAIGN),
    MUKAGO(QuestType.CAMPAIGN),
    WAKURABO(QuestType.CAMPAIGN),
    ROKURO(QuestType.CAMPAIGN),
    ENMU(QuestType.CAMPAIGN),
    KAIGAKU(QuestType.CAMPAIGN),
    GYUTARO(QuestType.CAMPAIGN);

    @Getter
    private final QuestType questType;

    Quests(final QuestType questType) {
        this.questType = questType;
    }

    public static List<Quests> filterType(QuestType questType) {
        return Arrays.stream(values()).filter(value -> value.getQuestType() == questType).toList();
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static Quests getEnum(String string) {
        for (Quests value : values()) {
            if (value.toString().replace("_", "-").equalsIgnoreCase(string.replace("_", "-")))
                return value;
        }
        return null;
    }
}
