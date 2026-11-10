package me.jammy.quests.listeners;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.event.FightStartEvent;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.boss.FightHandler;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.config.Cfgs;
import me.jammy.quests.util.config.Msgs;
import me.vaindev.demonslayer.DemonSlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDamageEvent;
import net.citizensnpcs.api.event.NPCSpawnEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BossListener implements Listener {

    private final OasisQuests plugin;
    private final UserManager userManager;

    public BossListener(final OasisQuests plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        QuestManager questManager = plugin.getQuestManager();
    }

    @EventHandler
    public void onBossDamage(EntityDamageEvent event) {
        Boss boss = plugin.getBossManager().get(event.getEntity());
        if (boss != null) {
            // Handle for Opponent Damaging Boss
            if (boss.getOpponent().isFrozen()) {
                event.setCancelled(true);
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
                return;
            }
            boss.damage(event.getDamage());
        }

        if (!(event.getEntity() instanceof Player player))
            return;
        boss = plugin.getBossManager().get(player);
        if (boss == null)
            return;

        // Handle for Boss Damaging Opponent
        if (boss.getBoss() == Bosses.ROKURO) {
            player.setVelocity(player.getLocation().getDirection().multiply(-2));
        }

    }

    @EventHandler
    public void onBossKill(EntityDeathEvent event) {
        Boss boss = plugin.getBossManager().get(event.getEntity());
        if (boss == null)
            return;
        boss.kill();
        event.getDrops().clear();
    }

    @EventHandler
    public void onNPCDamage(NPCDamageEvent event) {
        onBossDamage(event.getEvent());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        if (user.isFrozen())
            event.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        FightHandler handler = OasisQuests.instance().getFightHandler();
        Boss boss = plugin.getBossManager().get(user);
        if (boss != null && boss.isRunning())
            handler.end(boss, false);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getPlayer()))
            return;
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        FightHandler handler = OasisQuests.instance().getFightHandler();
        Boss boss = plugin.getBossManager().get(user);
        if (boss != null && boss.isRunning()) {
            handler.end(boss, false);
            event.setKeepInventory(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        User user = userManager.getUser(player);
        if (user == null)
            return;

        if (plugin.getKitHandler().hasUser(user)) {
            Msgs.of("<red>Your items are being returned").send(player);

            Cfgs locationsCfg = plugin.getLocationsCfg();
            String spawnWorld = locationsCfg.get().getString("spawn-world");
            World spawnWorldWorld = plugin.getServer().getWorld(spawnWorld);
            List<Integer> spawnCoords = locationsCfg.get().getIntegerList("spawn-coords");
            Location spawn = new Location(spawnWorldWorld, spawnCoords.get(0), spawnCoords.get(1), spawnCoords.get(2));
            player.teleport(spawn);
            plugin.getKitHandler().returnItems(user);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());
        if (user == null)
            return;

        List<String> commands = plugin.getBossCfg().get().getStringList("blocked-commands");
        if (commands.stream().anyMatch(c -> event.getMessage().toUpperCase(Locale.ROOT).contains(c.toUpperCase(Locale.ROOT)))) {
            if (plugin.getBossManager().get(user) == null)
                return;
            event.setCancelled(true);
            Msgs.of("<red>You cannot use this command during a boss battle!").send(event.getPlayer());
        }
    }

    @EventHandler
    public void onFightStart(FightStartEvent event) {
        plugin.getFightHandler().start(event.getBoss());
    }

    @EventHandler
    public void onNPCSpawn(NPCSpawnEvent event) {
        NPC npc = event.getNPC();
        npc.setProtected(true);
        String uuid = npc.data().get("opponent");
        if (uuid == null)
            return;
        Player player = Bukkit.getPlayer(UUID.fromString(uuid));
        if (player == null)
            return;
        npc.setProtected(false);
        LivingEntity entity = (LivingEntity) npc.getEntity();
        ItemStack nichirin = DemonSlayer.instance().getItemManager().createNichirin(null);
        entity.getEquipment().setItemInMainHand(nichirin);
        User user = plugin.getUserManager().getUser(player);
        if (user == null)
            return;

        Boss boss = plugin.getBossManager().get(user);
        if (boss == null)
            return;
        boss.setAttributes(entity);
    }
}
