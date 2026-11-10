package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Leap extends Attack {
    public Leap() {
        super(2);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        // Leap in the given direction.
        entity.getWorld().playSound(entity.getLocation(), sound, 1, 1);
        entity.setVelocity(entity.getLocation().getDirection().setY(0.25).multiply(2.5));
    }
}
