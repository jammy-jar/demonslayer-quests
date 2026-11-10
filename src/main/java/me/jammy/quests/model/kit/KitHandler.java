package me.jammy.quests.model.kit;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.config.Cfgs;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KitHandler {

    private final OasisQuests plugin;
    private final Cfgs playerDataConfig;

    public KitHandler(OasisQuests plugin) {
        this.plugin = plugin;
        playerDataConfig = plugin.getPlayerDataCfg();
    }

    public void giveKit(User user, Bosses boss) {
        Player player = user.getPlayer();
        PlayerInventory inventory = player.getInventory();
        Collection<PotionEffect> effects = player.getActivePotionEffects();
        playerDataConfig.set(player.getUniqueId() + ".health", player.getHealth());

        int i = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            playerDataConfig.set(player.getUniqueId() + ".items." + i, item);
            i++;
        }

        playerDataConfig.set(player.getUniqueId() + ".exp", player.getTotalExperience());

        i = 0;
        for (PotionEffect effect : effects) {
            playerDataConfig.get().set(player.getUniqueId() + ".effects." + i + ".type", effect.getType().getName());
            playerDataConfig.get().set(player.getUniqueId() + ".effects." + i + ".duration", effect.getDuration());
            playerDataConfig.get().set(player.getUniqueId() + ".effects." + i + ".amplifier", effect.getAmplifier());
            i++;
        }

        playerDataConfig.save();

        inventory.clear();

        ItemStack[] items = plugin.getKitManager().get(boss);
        i = 0;
        for (ItemStack item : items) {
            if (item == null) {
                i++;
                continue;
            }

            final String type = item.getType().name();
            if (type.endsWith("_HELMET"))
                inventory.setHelmet(item);
            else if (type.endsWith("_CHESTPLATE"))
                inventory.setChestplate(item);
            else if (type.endsWith("_LEGGINGS"))
                inventory.setLeggings(item);
            else if (type.endsWith("_BOOTS"))
                inventory.setBoots(item);
            else
                inventory.setItem(i, item);
            i++;
        }

        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(maxHealth);
        effects.forEach(effect -> player.removePotionEffect(effect.getType()));
    }

    public void returnItems(User user) {
        Player player = user.getPlayer();
        PlayerInventory inventory = player.getInventory();

        inventory.clear();

        int health = playerDataConfig.get().getInt(player.getUniqueId() + ".health");
        ConfigurationSection section = playerDataConfig.get().getConfigurationSection(player.getUniqueId() + ".items");
        if (section == null)
            return;
        Set<String> keys = section.getKeys(false);
        for (String key : keys) {
            ItemStack item = section.getItemStack(key);
            inventory.setItem(Integer.parseInt(key), item);
        }

        int exp = playerDataConfig.get().getInt(player.getUniqueId() + ".exp");
        player.setTotalExperience(exp);

        List<PotionEffect> effects = new ArrayList<>();
        section = playerDataConfig.get().getConfigurationSection(player.getUniqueId() + ".effects");
        if (section != null) {
            keys = section.getKeys(false);
            for (String key : keys) {
                ConfigurationSection effectConfig = section.getConfigurationSection(key);

                // retrieve the properties of the effect from the config
                PotionEffectType type = PotionEffectType.getByName(effectConfig.getString("type"));
                int duration = effectConfig.getInt("duration");
                int amplifier = effectConfig.getInt("amplifier");
                // create a new PotionEffect object from the retrieved properties
                effects.add(new PotionEffect(type, duration, amplifier));
            }
        }

        if (health > 20)
            player.setHealth(20);
        else
            player.setHealth(health);
        player.addPotionEffects(effects);

        playerDataConfig.set(player.getUniqueId().toString(), null);
        playerDataConfig.save();
    }

    public boolean hasUser(User user) {
        return (playerDataConfig.get().getConfigurationSection(user.getPlayer().getUniqueId().toString()) != null);
    }
}
