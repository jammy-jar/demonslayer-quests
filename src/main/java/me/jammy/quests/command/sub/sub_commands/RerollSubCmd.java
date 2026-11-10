package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class RerollSubCmd extends SubCommand {
    private final OasisQuests plugin;

    public RerollSubCmd(OasisQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reroll";
    }

    @Override
    public String getDescription() {
        return "Rerolls daily quests.";
    }

    @Override
    public List<String> getSyntax() {
        return List.of("reroll");
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
        plugin.getQuestManager().rerollQuests();
        Msgs.of("<dark_green>Successfully rerolled daily quests.").send(sender);
        return true;
    }
}
