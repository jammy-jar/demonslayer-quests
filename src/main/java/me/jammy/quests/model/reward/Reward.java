package me.jammy.quests.model.reward;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.util.config.Msgs;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Reward {

    private final OasisQuests plugin;

    private final List<ItemStack> items;
    private final List<List<String>> commands;
    private final int eco;
    private final int exp;

    public Reward(List<ItemStack> items, List<List<String>> commands, int eco, int exp) {
        this.plugin = OasisQuests.instance();
        this.items = items;
        this.commands = commands;
        this.eco = eco;
        this.exp = exp;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public List<List<String>> getCommands() {
        return commands;
    }

    public int getEco() {
        return eco;
    }

    public int getExp() {
        return exp;
    }

    public List<String> toStringList() {
        List<String> list = new ArrayList<>();
        if (eco > 0)
            list.add(" - <yellow>$" + eco);
        if (exp > 0)
            list.add(" - <yellow>" + exp + "xp");
        for (ItemStack item : items)
            list.add(" - <yellow>" + PlainTextComponentSerializer.plainText().serialize(item.displayName()));
        for (List<String> command : commands)
            list.add(" - <yellow>" + command.get(0));
        return list;
    }

    public void giveReward(Player player) {
        OasisQuests.getEconomy().depositPlayer(player, getEco());
        player.giveExp(getExp());

        for (List<String> command : getCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), Msgs.of(command.get(1)).var("player", player.getName()).asLegacy());
        }

        for (ItemStack item : getItems()) {
            player.getInventory().addItem(item);
        }
    }
}
