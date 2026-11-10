package me.jammy.quests.model.boss;

import lombok.Getter;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.requirement.RequirementType;
import org.bukkit.entity.EntityType;

import java.util.Locale;

public enum Bosses {
    KYOGAI("<red>Kyogai", Quests.KYOGAI, 5, RequirementType.KYOGAI_KY, EntityType.IRON_GOLEM),
    RUI("<dark_purple>Rui", Quests.RUI, 5, RequirementType.RUI_RU, EntityType.SPIDER),
    MUKAGO("<gold>Mukago", Quests.MUKAGO, 6, RequirementType.MUKAGO_MU, EntityType.ZOMBIE),
    WAKURABO("<yellow>Wakurabo", Quests.WAKURABO, 4, RequirementType.WAKURABO_WA, EntityType.HUSK),
    ROKURO("<dark_red>Rokuro", Quests.ROKURO, 10, RequirementType.ROKURO_RO, EntityType.ZOMBIFIED_PIGLIN),
    ENMU("<dark_green>Enmu", Quests.ENMU, 4, RequirementType.ENMU_EN, EntityType.DROWNED),
    KAIGAKU("<blue>Kaigaku", Quests.KAIGAKU, 4, RequirementType.KAIGAKU_KA, EntityType.PLAYER),
    GYUTARO("<aqua>Gyutaro", Quests.GYUTARO, 8, RequirementType.GYUTARO_GY, EntityType.WARDEN),
    DAKI("<dark_red>Daki", Quests.GYUTARO, 4, RequirementType.GYUTARO_GY, EntityType.HUSK);

    @Getter
    private final String displayName;
    @Getter
    private final Quests quest;
    @Getter
    private final double attackDamage;
    @Getter
    private final RequirementType bossRequirement;
    @Getter
    private final EntityType entityType;

    Bosses(final String displayName, final Quests quest, final double attackDamage, final RequirementType bossRequirement, final EntityType entityType) {
        this.displayName = displayName;
        this.quest = quest;
        this.attackDamage = attackDamage;
        this.bossRequirement = bossRequirement;
        this.entityType = entityType;
    }

    public String getDescription() {
        return OasisQuests.instance().getBossCfg().get().getString(this + ".description");
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static Bosses getEnum(String string) {
        for (Bosses value : values()) {
            if (value.toString().replace("_", "-").equalsIgnoreCase(string.replace("_", "-")))
                return value;
        }
        return null;
    }
}
