package me.jammy.quests.model.phase;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.config.Cfgs;
import org.bukkit.configuration.ConfigurationSection;

public class PhaseParser {

    public static double initHealth(Bosses boss, int phase) {
        Cfgs bossCfg = OasisQuests.instance().getBossCfg();

        ConfigurationSection section = bossCfg.get().getConfigurationSection(boss + ".phase-" + phase);
        return section.getDouble("health");
    }

    public static long initAttackDelay(Bosses boss, int phase) {
        Cfgs bossCfg = OasisQuests.instance().getBossCfg();

        ConfigurationSection section = bossCfg.get().getConfigurationSection(boss + ".phase-" + phase);
        return section.getLong("attack-delay");
    }

    public static double initDamage(Bosses boss, int phase) {
        Cfgs bossCfg = OasisQuests.instance().getBossCfg();

        ConfigurationSection section = bossCfg.get().getConfigurationSection(boss + ".phase-" + phase);
        return section.getDouble("damage");
    }
}
