package me.jammy.quests.conf;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.util.config.Cfgs;

import java.util.List;
import java.util.UUID;

public class NpcVisitsConf {
    private final Cfgs npcVisitsCfg;

    public NpcVisitsConf(OasisQuests plugin) {
        this.npcVisitsCfg = plugin.getNpcVisitsCfg();
    }

    public boolean add(UUID uuid, int npc) {
        List<Integer> list = npcVisitsCfg.get().getIntegerList(uuid.toString());
        if (list.contains(npc))
            return false;
        list.add(npc);
        npcVisitsCfg.set(uuid.toString(), list).save();
        return true;
    }
}
