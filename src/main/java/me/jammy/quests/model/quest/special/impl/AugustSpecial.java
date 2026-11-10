package me.jammy.quests.model.quest.special.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.special.Special;
import org.bukkit.Material;

public class AugustSpecial extends Special {
    public AugustSpecial() {
        super(Quests.AUGUST_SPECIAL, "<gold>August Special", Material.SUNFLOWER);
    }
}
