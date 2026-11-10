package me.jammy.quests.model.quest.daily.impl;

import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.daily.Daily;
import org.bukkit.Material;

public class Swim extends Daily {
    public Swim() {
        super(Quests.SWIM, "<yellow>Swim", Material.AXOLOTL_BUCKET);
    }
}
