package me.jammy.quests.model.reward;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MissionReward extends Reward {

    @Getter private final int level;

    public MissionReward(final int level, final List<ItemStack> items, final List<List<String>> commands, final int eco, final int exp) {
        super(items, commands, eco, exp);
        this.level = level;
    }
}
