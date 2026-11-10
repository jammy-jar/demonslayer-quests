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

public class GroundStrike extends Attack {
    public GroundStrike() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        entity.setGravity(false);
        entity.setAI(false);
        jump(damage, opponent, entity, particle, sound);
    }

    private void jump(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        Vector bossLoc = entity.getLocation().toVector();
        Vector opponentLoc = opponent.getLocation().toVector();
        Vector destination = opponentLoc.clone();

        // Get the horizontal distance between the player and the boss (ignores y-axis).
        Vector vector = destination.clone().subtract(bossLoc).setY(0);
        double distance = vector.distance(new Vector());

        int steps = 10;
        Vector step = vector.multiply(1d / steps);
        double ymax = 5;

        Vector finalLoc = destination.clone().add(new Vector(0, ymax, 0));

        new BukkitRunnable() {
            // Imagine a vertical ellipse centred at the origin of height (above O) 5 and width (to the right of O) the horizontal distance between the player and the boss.
            int i = 0;
            double x = distance;
            Location currentLoc = entity.getLocation();
            public void run() {
                x = x - x / steps;
                if (i >= steps || x < 0) {
                    groundStrike(damage, opponent, entity, particle, sound, finalLoc, destination);
                    cancel();
                    return;
                }
                currentLoc.add(step);
                double y = ymax * Math.sqrt( (x * x) / (distance * distance) + 1 );
                currentLoc.setY(destination.getY() + y);
                entity.teleport(currentLoc);
                i++;
            }
        }.runTaskTimer(OasisQuests.instance(), 0L, 1L);
    }

    private void groundStrike(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound, final Vector finalLoc, final Vector destination) {
        int steps = 5;
        Vector step = destination.clone();
        step.subtract(finalLoc).multiply(1d / steps);

        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i >= steps) {
                    cancel();
                    if (opponent.getLocation().distance(entity.getLocation()) < 5)
                        opponent.damage(damage * 6);
                    opponent.playSound(opponent.getLocation(), sound, 1, 1);
                    ParticleAPI.drawFilledCircle(entity.getLocation(), 4, particle);
                    entity.setGravity(true);
                    entity.setAI(true);
                    return;
                }
                Location currentLoc = entity.getLocation();
                currentLoc.add(step);
                entity.teleport(currentLoc);
                i++;
            }
        }.runTaskTimer(OasisQuests.instance(), 15L, 1L);
    }
}
