package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class BreedChickens extends Daily {
    public BreedChickens() {
        super(Quests.BREED_CHICKENS, "<yellow>Breed chickens", Material.WHEAT_SEEDS);
    }
}
