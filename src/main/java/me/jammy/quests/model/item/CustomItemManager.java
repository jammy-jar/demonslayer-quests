package me.jammy.quests.model.item;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.special.Special;
import me.jammy.quests.util.Keys;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class CustomItemManager {
    private final QuestManager questManager;

    private final NamespacedKey itemKey;
    private final NamespacedKey questKey;

    public CustomItemManager(OasisQuests plugin) {
        this.questManager = plugin.getQuestManager();

        this.itemKey = new NamespacedKey(plugin, "custom_item");
        this.questKey = new NamespacedKey(plugin, "quest");
    }

    public ItemStack getSpecialVoucher(String specialName) {
        Special special = questManager.getSpecial(Quests.getEnum(specialName));
        return ItemBuilder.start(Material.BOOK)
                .glow().name("<light_purple>Special Voucher: " + special.getDisplayName())
                .pdc(itemKey, PersistentDataType.STRING, "special_voucher")
                .pdc(questKey, PersistentDataType.STRING, specialName)
                .build();
    }

    public boolean isSpecialVoucher(ItemStack item) {
        if (item == null)
            return false;

        String customItem = Keys.CUSTOM_ITEM.read(item.getItemMeta());
        return customItem != null && customItem.equalsIgnoreCase("special_voucher");
    }
}
