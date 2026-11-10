package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Slumber extends Attack {
    public Slumber() {
        super(0.05);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 600, 5, true));
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 600, 2, true));
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 600, 2, true));
        Msgs.of("<dark_red>You have entered a slumber!").send(opponent);
    }
}
