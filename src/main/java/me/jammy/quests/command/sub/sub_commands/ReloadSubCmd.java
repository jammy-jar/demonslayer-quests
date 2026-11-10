package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.util.config.Cfgs;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCmd extends SubCommand {
    private final OasisQuests plugin;

    public ReloadSubCmd(OasisQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getSyntax() {
        return List.of("reload");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(String[] args) {
        return new ArrayList<>();
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        Cfgs.reloadAll();
        Msgs.of("<dark_green>Successfully reloaded the plugin.").send(sender);
        return true;
    }
}
