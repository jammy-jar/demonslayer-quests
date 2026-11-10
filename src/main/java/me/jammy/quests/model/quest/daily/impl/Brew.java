package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class Brew extends Daily {
    public Brew() {
        super(Quests.BREW, "<yellow>Brew Potions", Material.POTION);
    }
}
