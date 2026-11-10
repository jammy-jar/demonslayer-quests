package me.jammy.quests.listeners;


import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.item.CustomItemManager;
import me.jammy.quests.model.item.impl.SpecialVoucher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ItemUseListener implements Listener {
    private final CustomItemManager itemManager;

    public ItemUseListener(OasisQuests plugin) {
        this.itemManager = plugin.getCustomItemManager();
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick())
            return;

        ItemStack item = event.getItem();

        if (itemManager.isSpecialVoucher(item))
            new SpecialVoucher().use(event);
    }
}
