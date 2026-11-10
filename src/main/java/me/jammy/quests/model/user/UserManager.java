package me.jammy.quests.model.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.jammy.quests.DataAccess;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.event.PlayerOnlineEvent;
import me.jammy.quests.model.quest.daily.Daily;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class UserManager {
    private final Map<Player, User> users = Maps.newHashMap();
    private final OasisQuests plugin = OasisQuests.instance();

    public User getUser(final Player player) {
        if (player == null) return null;
        if (CitizensAPI.getNPCRegistry().isNPC(player))
            return null;
        User user = users.get(player);
        return user;
    }

    public Collection<User> users() {
        return users.values();
    }

    public void initUser(final Player player) {
        DataAccess.initUserQuests(player.getUniqueId());
    }

    public void addUser(final User user) {
        users.put(user.getPlayer(), user);
    }

    public void removeUser(final Player player) {
        users.remove(player);
    }

    public void updateUser(User user) {
        DataAccess.updateUser(user.getPlayer().getUniqueId(), user.getProgressCollection(), user.getActiveQuests());
    }

    public void updateUsers() {
        users.keySet().removeIf(u -> !u.isOnline());
        List<User> temp = Lists.newArrayList(users.values());
        for (User user : temp) {
            if (user == null)
                return;
            updateUser(user);
        }
    }

    public void resetDailys() {
        for (User user : users.values()) {
            user.resetDailyProgress();
            for (Daily daily : plugin.getQuestManager().dailys())
                user.setActiveQuest(daily, false);
        }
        DataAccess.resetDailys();
    }

    public void runUserUpdater() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.runTaskTimerAsynchronously(plugin, this::updateUsers, 600L, 2400L);
    }

    public void runPlaytime() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            users().forEach(user -> {
                if (user == null)
                    return;
                if (user.getPlayer() == null)
                    return;
                PlayerOnlineEvent event = new PlayerOnlineEvent(user.getPlayer());
                Bukkit.getPluginManager().callEvent(event);
            });
        }, 0, 20 * 60);
    }
}
