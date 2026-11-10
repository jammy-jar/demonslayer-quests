package me.jammy.quests.model.kit;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.config.Cfgs;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class KitManager {

    private final Cfgs kitCfg;

    public KitManager(OasisQuests plugin) {
        kitCfg = plugin.getKitCfg();
    }

    public void set(Bosses boss, ItemStack[] kit) {
        int i = 0;
        for (ItemStack item : kit) {
            kitCfg.set(boss.toString() + "." + i, item).save();
            i++;
        }
    }

    public ItemStack[] get(Bosses boss) {
        ItemStack[] kit = new ItemStack[36];
        ConfigurationSection section = kitCfg.get().getConfigurationSection(boss.toString());
        if (section == null)
            return kit;
        for (String key : section.getKeys(false))
            kit[Integer.parseInt(key)] = section.getItemStack(key);
        return kit;
    }
}
