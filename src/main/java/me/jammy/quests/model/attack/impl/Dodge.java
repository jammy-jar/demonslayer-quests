package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Dodge extends Attack {
    public Dodge() {
        super(0.25);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        Vector entDirection = entity.getLocation().getDirection().clone();
        int rotation = (int) (Math.random() * Math.PI);
        Vector newDirection = entDirection.setY(0).rotateAroundY(Math.PI / 2 + rotation);
        entity.teleport(entity.getLocation().clone().add(newDirection.multiply(4)));
    }
}
