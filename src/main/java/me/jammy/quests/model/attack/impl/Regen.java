package me.jammy.quests.model.attack.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.model.boss.Boss;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Regen extends Attack {
    public Regen() {
        super(0.5);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        Boss boss = OasisQuests.instance().getBossManager().get(entity);
        if (boss == null)
            return;
        Msgs.of("<dark_purple>Uh oh, " + boss.getDisplayName() + " <dark_purple>is Regenerating!!").send(opponent.getPlayer());
        double regen = damage * 15;
        boss.updateBossBar(-regen);
        if (entity.getHealth() + regen > entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
            entity.setHealth(entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
        entity.setHealth(entity.getHealth() + regen);
    }
}
