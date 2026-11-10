package me.jammy.quests.model.quest;

import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public enum QuestType {
    CAMPAIGN("red", ItemBuilder.nichirin().build()),
    DAILY("aqua", ItemBuilder.start(Material.PAPER).build()),
    SPECIAL("light_purple", ItemBuilder.start(Material.PAPER).glow().build()),
    // DEMON("dark_red", ItemBuilder.start(Material.ROTTEN_FLESH).glow().build()),
    MISSION("yellow", ItemBuilder.start(Material.GRASS_BLOCK).build()),
    STARTER("blue", ItemBuilder.skull()
            .value("e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZjNDM5NmEzNzc5N2VkNjMzNzU1YTgyOGYzMDBlM2Y0NTU1OWFkMTAyZTZhZGQ5ZTY2YjczNTRmZDNmOGJjNyJ9fX0=").build());

    private final String color;
    private final ItemStack icon;

    QuestType(final String color, final ItemStack icon) {
        this.color = color;
        this.icon = icon;
    }

    public String getFormattedName() {
        return "<" + color + ">" + WordUtils.capitalizeFully(this.name().replace("_", " "));
    }

    public ItemStack getIcon() {
        ItemStack icon = this.icon;
        ItemMeta meta = icon.getItemMeta();
        meta.displayName(Msgs.of(this.getFormattedName()).asComp());
        icon.setItemMeta(meta);
        return icon;
    }

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT).replace("_", "-");
    }

    public static QuestType getEnum(String string) {
        for (QuestType value : values()) {
            if (value.toString().replace("_", "-").equalsIgnoreCase(string.replace("_", "-")))
                return value;
        }
        return null;
    }
}
