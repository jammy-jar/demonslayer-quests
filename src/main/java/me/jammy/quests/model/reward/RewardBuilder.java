package me.jammy.quests.model.reward;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardBuilder {

    private List<ItemStack> items = new ArrayList<>();
    private List<List<String>> commands = new ArrayList<>();
    private int eco = 0;
    private int exp = 0;
    private int level = 0;

    public RewardBuilder eco(int eco) {
        this.eco = eco;
        return this;
    }

    public RewardBuilder exp(int exp) {
        this.exp = exp;
        return this;
    }

    public RewardBuilder items(List<ItemStack> items) {
        this.items = items;
        return this;
    }

    public RewardBuilder commands(List<List<String>> commands) {
        this.commands = commands;
        return this;
    }

    public RewardBuilder level(int level) {
        this.level = level;
        return this;
    }

    public Reward build() {
        if (level != 0)
            return new MissionReward(level, items, commands, eco, exp);
        else
            return new Reward(items, commands, eco, exp);
    }
}
