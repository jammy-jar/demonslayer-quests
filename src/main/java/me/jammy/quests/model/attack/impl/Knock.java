package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Knock extends Attack {
    public Knock() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        jumpBack(damage, opponent, entity, particle, sound);
    }

    private void jumpBack(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        Vector bossLoc = entity.getLocation().toVector();
        Vector opponentLoc = opponent.getLocation().toVector();

        // Get the horizontal distance between the player and the boss (ignores y-axis).
        Vector direction = opponentLoc.subtract(bossLoc).normalize();

        entity.setVelocity(direction.clone().setY(0.25).multiply(-2));

        new BukkitRunnable() {
            public void run() {
                jumpForward(damage, opponent, entity, particle, sound, direction);
                cancel();
            }
        }.runTaskLater(OasisQuests.instance(), 20L);
    }

    private void jumpForward(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound, final Vector direction) {
        opponent.playSound(opponent.getLocation(), sound, 1, 1);
        Location loc = entity.getLocation();
        for (int i = 0; i < 15; i++) {
            loc.add(direction.setY(0));
            if (opponent.getLocation().distance(loc) < 3) {
                opponent.damage(damage * 4);
                opponent.spawnParticle(particle, opponent.getLocation(), 5);
                opponent.setVelocity(direction.clone().rotateAroundY(90).setY(0.1).multiply(3));
                break;
            }
        }

        entity.setVelocity(direction.clone().setY(0.1).multiply(4));
    }
}
