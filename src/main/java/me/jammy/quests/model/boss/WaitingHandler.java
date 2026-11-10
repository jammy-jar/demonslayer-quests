package me.jammy.quests.model.boss;

import com.google.common.collect.Maps;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.event.FightStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class WaitingHandler {
    Map<Player, Boss> map = Maps.newHashMap();

    public void add(Player player, Boss boss) {
        map.put(player, boss);
    }

    public void runWaitingScheduler() {
        OasisQuests.instance().getServer().getScheduler().scheduleSyncRepeatingTask(OasisQuests.instance(), () -> {
            for (Map.Entry<Player, Boss> entry : map.entrySet()) {
                if (entry.getKey().getLocation().getWorld() != entry.getValue().getLocation().getWorld())
                    return;
                if (entry.getKey().getLocation().distance(entry.getValue().getLocation()) < OasisQuests.instance().getLocationsCfg().get().getDouble("range")) {
                    map.remove(entry.getKey());
                    FightStartEvent event = new FightStartEvent(entry.getKey(), entry.getValue());
                    Bukkit.getPluginManager().callEvent(event);
                }
            }
        }, 0L, 10L);
    }
}
