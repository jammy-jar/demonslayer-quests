package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.ParticleAPI;
import me.jammy.quests.util.config.Msgs;
import me.vaindev.demonslayer.OldParticleAPI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Electrokinesis extends Attack {
    public Electrokinesis() {
        super(0.25);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        warning(damage, opponent, entity, particle, sound);
    }

    private void warning(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        double range = 10;
        Location playerLoc = opponent.getLocation().clone();

        new BukkitRunnable() {
            int i = 0;
            public void run() {
                if (i == 4)
                    cancel();
                playerLoc.getWorld().spawnParticle(particle, playerLoc, 30);
                i++;
            }
        }.runTaskTimer(OasisQuests.instance(), 0, 5L);

        new BukkitRunnable() {
            public void run() {
                lightning(damage, opponent, playerLoc, particle, sound);
            }
        }.runTaskLater(OasisQuests.instance(), 20L);
    }

    private void lightning(final double damage, final Player opponent, final Location location, final Particle particle, final Sound sound) {
        opponent.playSound(location, sound, 1, 1);
        new OldParticleAPI(opponent).createLightning(opponent);

        if (location.distance(opponent.getLocation()) < 3) {
            opponent.damage(5 * damage);
            opponent.setFireTicks(200);
            opponent.addPotionEffect(PotionEffectType.WITHER.createEffect(200, 1));
            Msgs.of("<dark_red>Kaigaku's Electrokinesis has Rent and Burnt your skin!").send(opponent);
        }

        ParticleAPI.drawLine(location, location.clone().add(0, 7, 0), particle);
    }
}
