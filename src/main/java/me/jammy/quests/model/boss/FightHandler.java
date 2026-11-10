package me.jammy.quests.model.boss;

import com.google.common.collect.Maps;
import lombok.Getter;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.kit.KitHandler;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Cfgs;
import me.jammy.quests.util.config.Msgs;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FightHandler {
    private final OasisQuests plugin;
    private final QuestManager questManager;
    private final KitHandler kitHandler;
    private final Map<User, List<Integer>> map = Maps.newHashMap();
    @Getter private final Map<Integer, User> locationsInUse = Maps.newHashMap();
    private final Cfgs bossCfg;
    private final Cfgs locationsCfg;


    public FightHandler(OasisQuests plugin) {
        this.plugin = plugin;
        this.kitHandler = plugin.getKitHandler();
        this.questManager = plugin.getQuestManager();

        bossCfg = plugin.getBossCfg();
        locationsCfg = plugin.getLocationsCfg();
    }

    public void activate(Bosses bossType, User user) {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();

        List<Integer> tasks = new ArrayList<>();
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>Teleporting", user), 0));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>3", user), 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>2", user), 2 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>1", user), 3 * 20));

        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> teleportUser(user, bossType), 4 * 20));

        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>Teleporting...", user), 4 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>Starting Battle!!!", user), 7 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>3", user), 8 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>2", user), 9 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>1", user), 10 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<green><b>FIGHT!", user), 11 * 20));
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> announcement("<red><b>60 Seconds Remaining!", user), 11 * 20 + (bossCfg.get().getLong("max-fight-time") - 60) * 20));

        Boss boss = plugin.getBossSummoner().summon(bossType, user);
        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> {
            boss.getOpponent().setFrozen(false);
            OasisQuests.instance().getWaitingHandler().add(user.getPlayer(), boss);
        }, 11 * 20L));

        tasks.add(scheduler.scheduleSyncDelayedTask(plugin, () -> {
            if (map.get(user) != null)
                end(boss, false);
        }, (bossCfg.get().getLong("max-fight-time")) * 20));

        map.put(user, tasks);
    }

    private void announcement(String announcement, User user) {
        Msgs.of(announcement).title(user.getPlayer());
    }

    private void teleportUser(User user, Bosses boss) {
        ConfigurationSection locationsSection = locationsCfg.get().getConfigurationSection("arena-locations");

        List<Double> coords = null;
        for (String key : locationsSection.getKeys(false)) {
            String[] parts = key.split("-");
            int number = Integer.parseInt(parts[1]);
            if (locationsInUse.get(number) == null) {
                coords = locationsSection.getConfigurationSection(key).getDoubleList("user-spawn-coords");
                locationsInUse.put(number, user);
                break;
            }
        }
        if (coords == null)
            return;

        String bossWorld = locationsCfg.get().getString("boss-world");
        World world = plugin.getServer().getWorld(bossWorld);
        Location location = new Location(world, coords.get(0), coords.get(1), coords.get(2));
        user.getPlayer().teleport(location);

        user.setFrozen(true);
        kitHandler.giveKit(user, boss);
    }

    public void start(Boss boss) {
        boss.spawn(boss.getBoss().getEntityType());

        boss.setRunning(true);
    }

    public void end(Boss boss, boolean success) {
        User opponent = boss.getOpponent();

        List<Integer> tasks = map.get(opponent);
        if (tasks == null)
            return;
        map.remove(opponent);

        if (!boss.getEntity().isDead())
            boss.getEntity().setHealth(0);
        boss.getCurrentPhase().cancel();
        opponent.setFrozen(false);
        boss.setRunning(false);

        plugin.getBossManager().remove(boss);
        plugin.getMiniBossManager().get(boss).forEach(b -> {
            b.getEntity().setHealth(0);
            b.getCurrentPhase().cancel();
            b.setRunning(false);
        });

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(boss.getEntity());
        if (npc != null) {
            npc.destroy();
            npc.despawn();
        }

        int key = locationsInUse.entrySet()
                .stream().filter(e -> e.getValue().getPlayer().getUniqueId() == opponent.getPlayer().getUniqueId())
                .findFirst().orElse(Maps.immutableEntry(-1, null)).getKey();
        if (key == -1)
            return;
        locationsInUse.remove(key);

        Bosses bossType = boss.getBoss();
        tasks.forEach(t -> plugin.getServer().getScheduler().cancelTask(t));

        if (plugin.getUserManager().users().stream().anyMatch(matchingUser -> matchingUser.getPlayer().getUniqueId() == opponent.getPlayer().getUniqueId())) {
            Player player = opponent.getPlayer();
            opponent.getPlayer().hideBossBar(boss.getBossBar());
            plugin.getMiniBossManager().get(boss).forEach(b -> opponent.getPlayer().hideBossBar(b.getBossBar()));

            if (!player.isDead()) {
                String spawnWorld = locationsCfg.get().getString("spawn-world");
                World spawnWorldWorld = plugin.getServer().getWorld(spawnWorld);

                List<Double> spawnCoords = locationsCfg.get().getDoubleList("spawn-coords");
                if (spawnCoords.isEmpty())
                    return;
                Location spawn = new Location(spawnWorldWorld, spawnCoords.get(0), spawnCoords.get(1), spawnCoords.get(2));
                player.teleport(spawn);
                kitHandler.returnItems(opponent);
            }

            if (!success) {
                Msgs.of("<red><b>Fail! You failed the boss fight against " + boss.getDisplayName()).send(player);
                long cooldown = bossCfg.get().getLong(bossType.name().toLowerCase(Locale.ROOT) + ".cooldown") * 60;
                plugin.getCooldownManager().add(player.getUniqueId().toString(), bossType, cooldown);
                return;
            }

            Msgs.of("<green><b>Defeated! You defeated " + boss.getDisplayName()).send(player);

            Campaign quest = questManager.getCampaign(bossType.getQuest());
            boss.getOpponent().action(RequirementType.getEnum(quest.toString() + "_" + quest.toString().substring(0, 2)));
        }
    }
}
