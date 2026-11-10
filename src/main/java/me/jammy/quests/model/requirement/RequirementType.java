package me.jammy.quests.model.requirement;

import lombok.Getter;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum RequirementType {
    ADVENTURER(Quests.ADVENTURER, RequirementClassification.LEVELLABLE),
    ALCHEMIST(Quests.ALCHEMIST, RequirementClassification.LEVELLABLE),
    BREEDING(Quests.BREEDING, RequirementClassification.LEVELLABLE),
    ENCHANTER(Quests.ENCHANTER, RequirementClassification.LEVELLABLE),
    EXPERIENCE(Quests.EXPERIENCE, RequirementClassification.LEVELLABLE),
    FARMER(Quests.FARMER, RequirementClassification.LEVELLABLE),
    FISHER(Quests.FISHER, RequirementClassification.LEVELLABLE),
    MINER(Quests.MINER, RequirementClassification.LEVELLABLE),
    PLAYTIME(Quests.PLAYTIME, RequirementClassification.LEVELLABLE),
    SLAUGHTERER(Quests.SLAUGHTERER, RequirementClassification.LEVELLABLE),
    TAMER(Quests.TAMER, RequirementClassification.LEVELLABLE),
    WOODCUTTER(Quests.WOODCUTTER, RequirementClassification.LEVELLABLE),

    KILL_PLAYERS(Quests.KILL_PLAYERS, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS(Quests.KILL_DEMONS, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_WITH_FIST(Quests.KILL_DEMONS_WITH_FIST, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_WITH_BOW(Quests.KILL_DEMONS_WITH_BOW, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE(Quests.KILL_DEMONS_IN_WARZONE, RequirementClassification.QUANTIFIABLE),
    COLLECT_XP_FROM_ORES(Quests.COLLECT_XP_FROM_ORES, RequirementClassification.QUANTIFIABLE),
    KILL_PLAYERS_IN_WARZONE(Quests.KILL_PLAYERS_IN_WARZONE, RequirementClassification.QUANTIFIABLE),
    SWIM(Quests.SWIM, RequirementClassification.QUANTIFIABLE),
    FISH_FISH(Quests.FISH_FISH, RequirementClassification.QUANTIFIABLE),
    BREED_CHICKENS(Quests.BREED_CHICKENS, RequirementClassification.QUANTIFIABLE),
    BREW(Quests.BREW, RequirementClassification.QUANTIFIABLE),

    VISIT_UROKODAKI(Quests.MAIN_STARTER, RequirementClassification.QUANTIFIABLE),
    VOTE(Quests.MAIN_STARTER, RequirementClassification.PERMISSION),

    PLAYER_KILL_S3(Quests.SEASON_3, RequirementClassification.QUANTIFIABLE),
    DEMON_KILL_S3(Quests.SEASON_3, RequirementClassification.QUANTIFIABLE),
    PLAYTIME_S3(Quests.SEASON_3, RequirementClassification.QUANTIFIABLE),
    XP_S3(Quests.SEASON_3, RequirementClassification.QUANTIFIABLE),
    MISSION_TIERS_S3(Quests.SEASON_3, RequirementClassification.TIER),

    VILLAGE_VISIT_AM(Quests.ADVENTURER_MASTER, RequirementClassification.QUANTIFIABLE),
    TRAVEL_AM(Quests.ADVENTURER_MASTER, RequirementClassification.QUANTIFIABLE),
    BREW_SPEED_AM(Quests.ADVENTURER_MASTER, RequirementClassification.QUANTIFIABLE),
    ADVENTURER_TIER_AM(Quests.ADVENTURER_MASTER, RequirementClassification.BASIC) {
        @Override
        public boolean checkComplete(User user) {
            // Check adventurer is at least 15.
            return user.getLevel(OasisQuests.instance().getQuestManager().getMission(Quests.ADVENTURER)) >= 15;
        }
    },

    PLAYER_KILL_SL(Quests.SLAYER, RequirementClassification.QUANTIFIABLE),
    DEMON_KILL_SL(Quests.SLAYER, RequirementClassification.QUANTIFIABLE),
    NICHIRIN_FORGE_SL(Quests.SLAYER, RequirementClassification.QUANTIFIABLE),
    BREW_STRENGTH_SL(Quests.SLAYER, RequirementClassification.QUANTIFIABLE),

    PLAYER_KILL_AS(Quests.AUGUST_SPECIAL, RequirementClassification.QUANTIFIABLE),
    DEMON_KILL_AS(Quests.AUGUST_SPECIAL, RequirementClassification.QUANTIFIABLE),
    BREW_AS(Quests.AUGUST_SPECIAL, RequirementClassification.QUANTIFIABLE),
    TRAVEL_AS(Quests.AUGUST_SPECIAL, RequirementClassification.QUANTIFIABLE),
    MISSION_TIERS_AS(Quests.AUGUST_SPECIAL, RequirementClassification.TIER),


    MISSION_TIERS_KY(Quests.KYOGAI, RequirementClassification.TIER),
    KILL_DEMONS_KY(Quests.KYOGAI, RequirementClassification.QUANTIFIABLE),
    DO_TUTORIAL_KY(Quests.KYOGAI, RequirementClassification.PERMISSION),
    VOTE_KY(Quests.KYOGAI, RequirementClassification.PERMISSION),
    KYOGAI_KY(Quests.KYOGAI, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_RU(Quests.RUI, RequirementClassification.TIER),
    KILL_DEMONS_RU(Quests.RUI, RequirementClassification.QUANTIFIABLE),
    CLAIM_LAND_RU(Quests.RUI, RequirementClassification.BASIC) {
        @Override
        public boolean checkComplete(User user) {
            if (OasisQuests.instance().getServer().getPluginManager().getPlugin("Lands") == null)
                return true;
            return OasisQuests.instance().getLandsIntegration().getLandPlayer(user.getPlayer().getUniqueId()).ownsLand();
        }
    },
    RUI_RU(Quests.RUI, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_MU(Quests.MUKAGO, RequirementClassification.TIER),
    HAS_RANK_MU(Quests.MUKAGO, RequirementClassification.PERMISSION),
    KILL_DEMONS_MU(Quests.MUKAGO, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_MU(Quests.MUKAGO, RequirementClassification.QUANTIFIABLE),
    KILL_ENDERMEN_MU(Quests.MUKAGO, RequirementClassification.QUANTIFIABLE),
    MUKAGO_MU(Quests.MUKAGO, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_WA(Quests.WAKURABO, RequirementClassification.TIER),
    HAS_RANK_WA(Quests.WAKURABO, RequirementClassification.PERMISSION),
    KILL_DEMONS_WA(Quests.WAKURABO, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_WA(Quests.WAKURABO, RequirementClassification.QUANTIFIABLE),
    KILL_UNIQUE_PLAYERS_WA(Quests.WAKURABO, RequirementClassification.QUANTIFIABLE),
    WAKURABO_WA(Quests.WAKURABO, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_RO(Quests.ROKURO, RequirementClassification.TIER),
    HAS_RANK_RO(Quests.ROKURO, RequirementClassification.PERMISSION),
    KILL_DEMONS_RO(Quests.ROKURO, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_RO(Quests.ROKURO, RequirementClassification.QUANTIFIABLE),
    KILL_UNIQUE_PLAYERS_RO(Quests.ROKURO, RequirementClassification.QUANTIFIABLE),
    ROKURO_RO(Quests.ROKURO, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_EN(Quests.ENMU, RequirementClassification.TIER),
    HAS_RANK_EN(Quests.ENMU, RequirementClassification.PERMISSION),
    KILL_DEMONS_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_WITH_BOW_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),
    KILL_WITCHES_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),
    KILL_CREEPERS_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),
    ENMU_EN(Quests.ENMU, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_KA(Quests.KAIGAKU, RequirementClassification.TIER),
    HAS_RANK_KA(Quests.KAIGAKU, RequirementClassification.PERMISSION),
    KILL_DEMONS_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_WITH_BOW_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),
    KILL_ENDERMEN_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),
    KILL_MAGMACUBES_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),
    KAIGAKU_KA(Quests.KAIGAKU, RequirementClassification.QUANTIFIABLE),

    MISSION_TIERS_GY(Quests.GYUTARO, RequirementClassification.TIER),
    HAS_RANK_GY(Quests.GYUTARO, RequirementClassification.PERMISSION),
    KILL_DEMONS_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_IN_WARZONE_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    KILL_DEMONS_WITH_BOW_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    KILL_ENDERMEN_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    KILL_MAGMACUBES_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    KILL_SLIMES_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE),
    GYUTARO_GY(Quests.GYUTARO, RequirementClassification.QUANTIFIABLE);

    @Getter
    private final Quests quest;
    @Getter
    private final RequirementClassification classification;

    RequirementType(final Quests quest, final RequirementClassification classification) {
        this.quest = quest;
        this.classification = classification;
    }

    public static List<RequirementType> of(Quests quest) {
        return Arrays.stream(values()).filter(value -> value.getQuest() == quest).toList();
    }

    public static RequirementType getEnum(String string) {
        for (RequirementType value : values()) {
            if (value.toString().replace("_", "-").equalsIgnoreCase(string.replace("_", "-")))
                return value;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public boolean checkComplete(User user) {
        if (classification == RequirementClassification.QUANTIFIABLE)
            return user.getProgress(this) >= getAmount();
        else if (classification == RequirementClassification.LEVELLABLE) {
            int level = user.getLevel(OasisQuests.instance().getQuestManager().getMission(quest));
            if (level > RequirementParser.missionRequirements(this).size())
                return false;
            return user.getProgress(this) >= getAmount(level);
        } else if (classification == RequirementClassification.TIER)
            return OasisQuests.instance().getQuestManager().missions().stream().allMatch(mission -> user.getLevel(mission) >= RequirementParser.tier(this));
        else if (classification == RequirementClassification.PERMISSION)
            return user.getPlayer().hasPermission(RequirementParser.permission(this));
        return false;
    }

    public Component getDescription() {
        if (classification == RequirementClassification.BASIC)
            return Msgs.of("<gray><i>" + RequirementParser.description(this)).asComp();
        if (classification == RequirementClassification.TIER)
            return Msgs.of("<gray><i>" + RequirementParser.description(this))
                    .var("tier", RequirementParser.tier(this))
                    .asComp();

        return Msgs.of("<gray><i>" + RequirementParser.description(this))
                .var("amount", RequirementParser.requirement(this))
                .var("unit", RequirementParser.unit(this))
                .asComp();
    }

    public int getAmount() {
        if (classification == RequirementClassification.QUANTIFIABLE)
            return RequirementParser.requirement(this);
        else if (classification == RequirementClassification.TIER)
            return RequirementParser.tier(this);
        return 0;
    }

    public int getAmount(int level) {
        if (classification == RequirementClassification.LEVELLABLE) {
            if (level > RequirementParser.missionRequirements(this).size())
                return 0;
            return RequirementParser.missionRequirements(this).get(level - 1);
        }
        return 0;
    }

    public String getUnit() {
        if (classification == RequirementClassification.QUANTIFIABLE || classification == RequirementClassification.LEVELLABLE)
            return RequirementParser.unit(this);
        return null;
    }

}
