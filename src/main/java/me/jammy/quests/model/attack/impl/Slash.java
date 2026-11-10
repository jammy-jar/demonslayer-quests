package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.Math;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Slash extends Attack {
    public Slash() {
        super(0.5);
    }

    @Override
    public void perform(double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        // Create a vector between the boss and a nearby player.
        entity.getLocation().getDirection();
        Vector vector = Math.vectorBetween(entity.getLocation(), opponent.getLocation());

        // Create a location with the bosses position and the new vector.
        Location location = entity.getLocation();
        location.setDirection(vector);

        // Send a slash from the boss directed towards the player.
        entity.getWorld().playSound(entity.getLocation(), sound, 1, 1);
        sendSlash(damage, opponent, location, particle, entity);
    }

    private void sendSlash(final double damage, final Player opponent, final Location location, final Particle particle, final Entity entity) {
        // Set the distance the attack will travel.
        int range = 15;
        new BukkitRunnable() {
            private int i = range;
            private Location currentLocation = location;

            public void run() {
                // Cancel if 'i' is 0, meaning it's looped for the length of the range.
                if (i == 0) {
                    cancel();
                    return;
                }

                if (opponent.getWorld() != currentLocation.getWorld()) {
                    cancel();
                    return;
                }

                // Damage players near the 'slash'


                Boss boss = OasisQuests.instance().getBossManager().get(entity);
                if (boss != null && boss.getBoss() == Bosses.WAKURABO) {
                    opponent.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 40, 2, true));
                    if (opponent.getLocation().distance(currentLocation)  < 1.5)
                        opponent.damage(damage * 2);
                } else if (boss != null && boss.getBoss() == Bosses.GYUTARO) {
                    opponent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 4, true));
                    if (opponent.getLocation().distance(currentLocation) < 1.5)
                        opponent.damage(damage * 2);
                } else {
                    if (opponent.getLocation().distance(currentLocation)  < 3)
                        opponent.damage(damage * 3);
                }

                // Move the current location forward by 1 Block.
                currentLocation = currentLocation.add(location.getDirection());
                currentLocation.getWorld().spawnParticle(particle, location, 5);
                i--;
            }
        }.runTaskTimer(OasisQuests.instance(), 0L, 2L);
    }
}
