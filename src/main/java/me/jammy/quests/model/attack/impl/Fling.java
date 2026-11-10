package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Fling extends Attack {
    public Fling() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        int random = (int) (Math.random() * 5);
        Vector vector = switch (random) {
            case 0 -> new Vector(0, 0, 1);
            case 1 -> new Vector(0, 0, -1);
            case 2 -> new Vector(1, 0, 0);
            case 3 -> new Vector(-1, 0, 0);
            case 4 -> new Vector(0, 0.25, 0);
            default -> new Vector(0, 0, 0);
        };
        opponent.setVelocity(vector.multiply(2.5));
    }
}
