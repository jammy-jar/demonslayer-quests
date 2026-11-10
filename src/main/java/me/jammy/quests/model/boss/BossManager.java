package me.jammy.quests.model.boss;

import com.google.common.collect.Sets;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.user.User;
import org.bukkit.entity.Entity;

import java.util.Set;

public class BossManager {

    private final Set<Boss> bosses = Sets.newHashSet();

    public BossManager(final OasisQuests plugin) {
    }

    public void add(Boss boss) {
        bosses.add(boss);
    }

    public void remove(Boss boss) {
        bosses.remove(boss);
    }

    public Boss get(Entity entity) {
        return bosses.stream().filter(boss -> {
            if (boss.getEntity() == null)
                return false;
            return boss.getEntity().getUniqueId() == entity.getUniqueId();
        }).findFirst().orElse(null);
    }

    public Boss get(User opponent) {
        return bosses.stream().filter(boss -> boss.getOpponent().getPlayer().getUniqueId() == opponent.getPlayer().getUniqueId())
                .findFirst().orElse(null);
    }
}
