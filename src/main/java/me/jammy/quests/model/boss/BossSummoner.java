package me.jammy.quests.model.boss;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.miniboss.MiniBoss;
import me.jammy.quests.model.miniboss.MiniBosses;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Cfgs;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class BossSummoner {
    private final OasisQuests plugin;
    private final Cfgs locationsCfg;

    public BossSummoner(OasisQuests plugin) {
        this.plugin = plugin;
        locationsCfg = plugin.getLocationsCfg();
    }

    public Boss summon(Bosses bossType, User user) {
        ConfigurationSection locationsSection = locationsCfg.get().getConfigurationSection("arena-locations");

        List<Double> coords = null;
        for (String key : locationsSection.getKeys(false)) {
            String[] parts = key.split("-");
            int number = Integer.parseInt(parts[1]);
            if (plugin.getFightHandler().getLocationsInUse().get(number) == null) {
                coords = locationsSection.getConfigurationSection(key).getDoubleList("boss-spawn-coords");
                break;
            }
        }
        if (coords == null)
            return null;

        String name = bossType.getDisplayName();

        String world = locationsCfg.get().getString("boss-world");
        World bossWorld = plugin.getServer().getWorld(world);
        Location location = new Location(bossWorld, coords.get(0), coords.get(1), coords.get(2));

        Boss boss = new Boss(bossType, name, location, user);
        plugin.getBossManager().add(boss);
        return boss;
    }

    public MiniBoss summon(Boss parent, MiniBosses bossType) {
        String name = bossType.getDisplayName();
        MiniBoss boss = new MiniBoss(bossType.getBossType(), bossType, parent, name);
        plugin.getBossManager().add(boss);
        plugin.getMiniBossManager().add(boss);
        return boss;
    }
}
