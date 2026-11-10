package me.jammy.quests.listeners;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.boss.BossManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.config.Cfgs;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Locale;

public class GeneralListener implements Listener {

    private final OasisQuests plugin;
    private final BossManager bossManager;
    private final UserManager userManager;

    public GeneralListener(OasisQuests plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.bossManager = plugin.getBossManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        userManager.initUser(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(OasisQuests.instance(), () -> {
            User user = userManager.getUser(player);
            if (user == null)
                return;

            if (plugin.getKitHandler().hasUser(user)) {
                try {
                    Msgs.of("<red>You left the game during a boss fight. Your items are being returned").send(player);
                } catch (Exception ignored) {
                }

                Cfgs locationsCfg = plugin.getLocationsCfg();
                String spawnWorld = locationsCfg.get().getString("spawn-world");
                World spawnWorldWorld = plugin.getServer().getWorld(spawnWorld);
                List<Integer> spawnCoords = locationsCfg.get().getIntegerList("spawn-coords");
                Location spawn = new Location(spawnWorldWorld, spawnCoords.get(0), spawnCoords.get(1), spawnCoords.get(2));
                player.teleport(spawn);
                plugin.getKitHandler().returnItems(user);
            }
        }, 20L);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        User user = userManager.getUser(player);
        if (user == null)
            return;
        userManager.updateUser(user);
        userManager.removeUser(player);

        Boss boss = bossManager.get(user);
        if (boss != null && boss.isRunning()) {
            OasisQuests.instance().getFightHandler().end(boss, false);
            long cooldown = plugin.getBossCfg().get().getLong(boss.getBoss().name().toLowerCase(Locale.ROOT) + ".cooldown") * 60;
            plugin.getCooldownManager().add(player.getUniqueId().toString(), boss.getBoss(), cooldown);
        }
    }
}
