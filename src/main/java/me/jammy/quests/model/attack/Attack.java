package me.jammy.quests.model.attack;

import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class Attack {
    @Getter
    private final double probability;

    protected Attack(final double probability) {
        this.probability = probability;
    }

    public abstract void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound);
}
