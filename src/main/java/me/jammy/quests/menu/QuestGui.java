package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class QuestGui extends Gui {
    public QuestGui(@NotNull final Player player, QuestType type) {
        super(player, type.toString() + "_menu", Msgs.of(type.getFormattedName()).asLegacy(), 3);
    }
}
