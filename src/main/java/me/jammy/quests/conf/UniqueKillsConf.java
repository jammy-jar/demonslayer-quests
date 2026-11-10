package me.jammy.quests.conf;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.util.config.Cfgs;

import java.util.List;
import java.util.UUID;

public class UniqueKillsConf {
    private final Cfgs uniqueKillsConf;

    public UniqueKillsConf(OasisQuests plugin) {
        this.uniqueKillsConf = plugin.getUniqueKillsCfg();
    }

    public boolean add(UUID uuid, RequirementType requirementType, UUID targetUuid) {
        List<String> list = uniqueKillsConf.get().getStringList(uuid + "." + requirementType);
        if (list.contains(targetUuid.toString()))
            return false;
        list.add(targetUuid.toString());
        uniqueKillsConf.set(uuid + "." + requirementType, list).save();
        return true;
    }
}
