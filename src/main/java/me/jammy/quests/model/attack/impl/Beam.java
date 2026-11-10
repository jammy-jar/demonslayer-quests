package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.ParticleAPI;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Beam extends Attack {
    public Beam() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        ParticleAPI.drawLine(entity.getLocation(), opponent.getLocation(), particle);
        opponent.damage(damage);
    }
}
