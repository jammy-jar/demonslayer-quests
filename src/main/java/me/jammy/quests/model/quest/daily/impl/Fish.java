package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class Fish extends Daily {
    public Fish() {
        super(Quests.FISH_FISH, "<yellow>Fish for fish.", Material.TROPICAL_FISH_BUCKET);
    }
}
