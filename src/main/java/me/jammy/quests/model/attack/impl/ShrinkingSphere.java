package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.ParticleAPI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ShrinkingSphere extends Attack {

    public ShrinkingSphere() {
        super(0.5);
        // Make probability in phases adjustable through config.
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        shrinkingSphere(opponent, opponent.getLocation(), 6, damage, 10L, particle);
        opponent.playSound(opponent.getLocation(), sound, 1, 1);
    }

    public static void shrinkingSphere(Player player, Location location, double radius, double damage, long interval, Particle particle) {
        new BukkitRunnable() {
            double r = radius;
            public void run() {
                ParticleAPI.drawSphere(location, r, particle);
                r = r - 0.5;
                if (player.getWorld() != location.getWorld()) {
                    cancel();
                    return;
                }
                if (player.getLocation().distance(location) > r / 2) {
                    cancel();
                    return;
                }
                player.damage(damage * 2);
            }
        }.runTaskTimer(OasisQuests.instance(), 0L, interval);
    }
}
