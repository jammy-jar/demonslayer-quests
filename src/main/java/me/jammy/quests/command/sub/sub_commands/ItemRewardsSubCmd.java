package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.menu.RewardItemsMenu;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ItemRewardsSubCmd extends SubCommand {
    @Override
    public String getName() {
        return "itemrewards";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getSyntax() {
        return List.of("itemrewards");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player player)) {
            Msgs.of("<red>You must be a player to do this!").send(sender);
            return true;
        }
        new RewardItemsMenu(player).open();
        return true;
    }
}
