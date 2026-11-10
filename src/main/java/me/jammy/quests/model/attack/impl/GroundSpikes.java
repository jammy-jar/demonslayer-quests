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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GroundSpikes extends Attack {

    public GroundSpikes() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        warning(damage, opponent, entity, particle, sound);
    }

    private void warning(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        double range = 10;
        Location playerLoc = opponent.getLocation().clone();

        double multiplier = playerLoc.distance(opponent.getLocation()) / 10;
        Vector vector = playerLoc.clone().toVector().subtract(opponent.getLocation().clone().toVector());
        opponent.setVelocity(vector.multiply(multiplier).setY(0.1));

        double cornerX = playerLoc.getX() - range;
        double cornerZ = playerLoc.getZ() - range;

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            double x = 2 * range * Math.random();
            double z = 2 * range * Math.random();

            double newX = cornerX + x;
            double newZ = cornerZ + z;

            locations.add(new Location(playerLoc.getWorld(), newX, playerLoc.getY(), newZ));
        }

        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i == 4)
                    cancel();
                for (Location location : locations)
                    playerLoc.getWorld().spawnParticle(particle, location.clone().add(0, 1, 0), 10);
                i++;
            }
        }.runTaskTimer(OasisQuests.instance(), 0, 5L);

        new BukkitRunnable() {
            public void run() {
                for (Location location : locations)
                    spike(damage, opponent, location, particle, sound);
            }
        }.runTaskLater(OasisQuests.instance(), 40L);
    }

    private void spike(final double damage, final Player opponent, final Location location, final Particle particle, final Sound sound) {
        opponent.playSound(location, sound, 1, 1);
        if (location.distance(opponent.getLocation()) < 1)
            opponent.damage(7.5 * damage);

        ParticleAPI.drawLine(location, location.clone().add(0, 7, 0), particle);
    }
}
