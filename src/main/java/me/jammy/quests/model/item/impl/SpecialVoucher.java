package me.jammy.quests.model.item.impl;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.item.CustomItem;
import me.jammy.quests.util.Keys;
import me.jammy.quests.util.config.Msgs;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialVoucher extends CustomItem {
    @Override
    public void use(PlayerInteractEvent event) {
        event.setCancelled(true);

        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        String special = Keys.QUEST.read(item.getItemMeta());
        if (player.hasPermission("quests.special." + special)) {
            Msgs.of("<red>You already have this Special Quest!").send(player);
            return;
        }

        item.subtract();
        OasisQuests.getLuckPerms().getUserManager().modifyUser(player.getUniqueId(), user ->
                user.data().add(Node.builder("quests.special." + special).build()));
    }
}
