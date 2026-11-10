package me.jammy.quests.model.miniboss;

import com.google.common.collect.Sets;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.user.User;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.Set;

public class MiniBossManager {
    private final Set<MiniBoss> bosses = Sets.newHashSet();

    public MiniBossManager(final OasisQuests plugin) {
    }

    public void add(MiniBoss boss) {
        bosses.add(boss);
    }

    public void remove(MiniBoss boss) {
        bosses.remove(boss);
    }

    public MiniBoss get(Entity entity) {
        return bosses.stream().filter(boss -> {
            if (boss.getEntity() == null)
                return false;
            return boss.getEntity().getUniqueId() == entity.getUniqueId();
        }).findFirst().orElse(null);
    }

    public MiniBoss get(User opponent) {
        return bosses.stream().filter(boss -> boss.getOpponent().getPlayer().getUniqueId() == opponent.getPlayer().getUniqueId())
                .findFirst().orElse(null);
    }

    public List<MiniBoss> get(Boss boss) {
        return bosses.stream().filter(m -> m.getParent().equals(boss)).toList();
    }
}
