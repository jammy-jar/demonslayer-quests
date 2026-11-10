package me.jammy.quests.model.attack.impl;

import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.config.Msgs;
import me.vaindev.demonslayer.DemonSlayer;
import me.vaindev.demonslayer.model.breath.Breath;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class ThunderBreathing extends Attack {
    public ThunderBreathing() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        if (!(entity instanceof Player player)) {
            if (entity.customName() == null)
                return;
            Msgs.of(MiniMessage.miniMessage().serialize(entity.customName()) + " is not a player!");
            return;
        }

        Breath thunder = DemonSlayer.instance().getBreathManager().getBreath("thunder");
        int form = (int) (thunder.forms() * Math.random()) + 1;
        thunder.executeForm(player, form);
    }
}
