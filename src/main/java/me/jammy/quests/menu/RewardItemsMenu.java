package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RewardItemsMenu extends Gui {

    private final PaginationManager paginationManager = new PaginationManager(this);

    private final OasisQuests plugin = OasisQuests.instance();
    private final RewardManager rewardManager = plugin.getRewardManager();
    private final Map<String, ItemStack> rewardItems = rewardManager.getItems();

    private final ItemStack nextItem;
    private final ItemStack prevItem;

    public RewardItemsMenu(@NotNull final Player player) {
        super(player, "rewarditems_menu", Msgs.of("<blue>Reward Items").asLegacy(), 4);
        paginationManager.registerPageSlotsBetween(0, 26);

        nextItem = InvIcon.NEXT.asBuilder()
                .name("<gray>Next Page")
                .build();
        prevItem = InvIcon.PREV.asBuilder()
                .name("<gray>Previous Page")
                .build();

    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon cancelIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Cancel").build()).onClick(e -> e.getInventory().close());
        addItem(27, cancelIcon);

        Icon confirmIcon = new Icon(InvIcon.CONFIRM.asBuilder().name("<green>Apply").build()).onClick(this::apply);
        addItem(35, confirmIcon);

        for (ItemStack item : rewardItems.values())
            paginationManager.addItem(new Icon(item));

        paginationManager.update();

        if (paginationManager.getLastPage() > 0) {
            Icon saveIcon = new Icon(nextItem).onClick(e -> paginationManager.goNextPage().update());
            addItem(32, saveIcon);
            saveIcon = new Icon(prevItem).onClick(e -> paginationManager.goPreviousPage().update());
            addItem(30, saveIcon);
        }
    }

    public void apply(InventoryClickEvent event) {
        List<String> toRemove = new ArrayList<>();

        for (String key : rewardItems.keySet()) {
            ItemStack item = rewardItems.get(key);
            if (Arrays.stream(event.getInventory().getContents()).noneMatch(invItem -> invItem != null && invItem.equals(item))) {
                toRemove.add(key);
            }
        }
        toRemove.forEach(rewardItems::remove);

        for (ItemStack newItem : event.getInventory().getContents()) {
            if (newItem == null || newItem.getType() == Material.PLAYER_HEAD || newItem.equals(ItemBuilder.filler(DyeColor.GRAY).build()))
                continue;
            if (!rewardItems.isEmpty()) {
                if (rewardItems.values().stream().anyMatch(item -> item.equals(newItem)))
                    continue;
            }

            int i = 0;
            while (rewardItems.get(String.valueOf(i)) != null)
                i++;
            rewardItems.put(String.valueOf(i), newItem);
        }

        rewardManager.saveItems(rewardItems, toRemove);
        Msgs.of("<green>Successfully updated reward items!").send(event.getWhoClicked());
        event.getInventory().close();
    }

    @Override
    public boolean onClick(InventoryClickEvent e) {
        return e.getSlot() <= 26;
    }
}
