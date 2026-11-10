package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Torture extends Attack {
    public Torture() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 5, true));
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 5, true));
        stab(damage, opponent);
    }

    private void stab(double damage, final Player opponent) {
        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i == 10)
                    cancel();
                opponent.damage(damage * 0.333);
                i++;
            }
        }.runTaskTimer(OasisQuests.instance(), 4L, 4L);
    }
}
