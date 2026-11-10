package me.jammy.quests.model.cooldown;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.config.Cfgs;

import java.util.Locale;

public class CooldownManager {

    private final Cfgs cooldownConfig;

    public CooldownManager(OasisQuests plugin) {
        this.cooldownConfig = plugin.getCooldownCfg();
    }

    public void add(String uuid, Bosses boss, long time) {
        long end = System.currentTimeMillis() + 1000 * time;
        cooldownConfig.set(uuid + "." + boss.name().toLowerCase(Locale.ROOT), end).save();
    }

    public long get(String uuid, Bosses boss) {
        long now = System.currentTimeMillis();
        long end = cooldownConfig.get().getLong(uuid + "." + boss.name().toLowerCase(Locale.ROOT));

        if (end - now <= 0) {
            cooldownConfig.set(uuid + "." + boss.name().toLowerCase(Locale.ROOT), null).save();
            return 0;
        }

        return (end - now) / 1000;
    }

    public String getString(String uuid, Bosses boss) {
        long cooldown = get(uuid, boss);
        if (cooldown < 60)
            return cooldown + "s";
        if (cooldown < 3600)
            return cooldown / 60 + "m";
        if (cooldown < 86400)
            return cooldown / 3600 + "h";
        return cooldown / 86400 + "d";
    }
}
