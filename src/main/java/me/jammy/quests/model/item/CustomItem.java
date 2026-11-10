package me.jammy.quests.model.item;

import me.jammy.quests.OasisQuests;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class CustomItem {
    protected final OasisQuests plugin = OasisQuests.instance();

    public abstract void use(PlayerInteractEvent event);
}
