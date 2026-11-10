package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.ParticleAPI;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Spikes extends Attack {

    public Spikes() {
        super(0.5);
    }


    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        entity.setVelocity(new Vector(0, 0.25, 0));
        new BukkitRunnable() {
            public void run() {
                summonSpikes(damage, opponent, entity, particle, sound);
            }
        }.runTaskLater(OasisQuests.instance(), 20L);
    }

    private void summonSpikes(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        opponent.playSound(opponent.getLocation(), sound, 1, 1);

        Vector direction = opponent.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();

        int radius = 6;
        if (opponent.getLocation().distance(entity.getLocation()) < radius) {
            opponent.damage(damage * 4);
            opponent.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 2, true));
            opponent.spawnParticle(particle, opponent.getLocation(), 5);
            opponent.setVelocity(direction.clone().multiply(2).setY(0.1));
        }
        ParticleAPI.drawSpikes(entity.getLocation(), radius, particle);
        entity.setAI(true);
    }
}
