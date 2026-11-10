package me.jammy.quests.model.attack.impl;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.InventoryAPI;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.JumpscareMenu;
import me.jammy.quests.model.attack.Attack;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Sleep extends Attack {
    public Sleep() {
        super(0.1);
    }

    @Override
    public void perform(final double damage, final Player opponent, final LivingEntity entity, final Particle particle, final Sound sound) {
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 10, true));
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2, true));
        opponent.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40, 10, true));
        opponent.sleep(opponent.getLocation(), true);

        BukkitScheduler scheduler = OasisQuests.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(OasisQuests.instance(), () -> {
            new JumpscareMenu(opponent).open();
            opponent.playSound(opponent, sound, 1, 1);
            Msgs.of("<dark_red>Enmu is manipulating your dreams!").send(opponent);
        }, 30L);
        scheduler.scheduleSyncDelayedTask(OasisQuests.instance(), () -> {
            // TODO: Add jumpscare texture.
            Gui menu = InventoryAPI.getInstance().getGuiFromInventory(opponent.getOpenInventory().getTopInventory());
            if (menu == null)
                return;
            if (menu.getId().equalsIgnoreCase("jumpscare"))
                menu.getInventory().close();
            opponent.sleep(opponent.getLocation(), false);
        }, 35L);
    }
}
