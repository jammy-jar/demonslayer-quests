package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.kit.KitManager;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class KitMenu extends Gui {

    private final Bosses boss;
    private final KitManager kitManager;

    public KitMenu(Player player, Bosses boss) {
        super(player, "kit_menu", Msgs.of(boss.getDisplayName() + " Kit").asLegacy(), 5);
        this.boss = boss;
        OasisQuests plugin = OasisQuests.instance();
        this.kitManager = plugin.getKitManager();
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillRow(new Icon(ItemBuilder.filler(DyeColor.GRAY).build()), 4);

        ItemStack[] kit = kitManager.get(boss);
        int i = 0;
        for (ItemStack item : kit) {
            addItem(i, item);
            i++;
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        kitManager.set(boss, Arrays.copyOfRange(event.getInventory().getContents(), 0, 36));
    }

    @Override
    public boolean onClick(InventoryClickEvent e) {
        return e.getSlot() <= 35;
    }
}
