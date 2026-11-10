package me.jammy.quests.model.quest.special.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.special.Special;
import org.bukkit.Material;

public class Slayer extends Special {
    public Slayer() {
        super(Quests.SLAYER, "<aqua>Slayer", Material.NETHERITE_SWORD);
    }
}
