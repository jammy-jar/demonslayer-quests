
package me.jammy.quests.util.config;

import me.jammy.quests.OasisQuests;
import org.bukkit.configuration.Configuration;

import java.io.File;
import java.util.List;

public class QuestDataCfg {

    private static final OasisQuests plugin = OasisQuests.instance();
    private static final Cfgs activeQuestsCfg = Cfgs.of(new File(plugin.getDataFolder(), "active-quests-data.yml"));
    private static final Configuration activeQuestsConfig = activeQuestsCfg.get();

    public static void setTodaysDailys(List<String> dailys) {
        activeQuestsConfig.set("todays-dailys", dailys);
        activeQuestsCfg.save();
    }

    public static List<String> fetchTodaysDailys() {
        return activeQuestsConfig.getStringList("todays-dailys");
    }
}
