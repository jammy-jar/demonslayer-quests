package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StartFightSubCmd extends SubCommand {
    private final OasisQuests plugin;
    private final UserManager userManager;

    public StartFightSubCmd(OasisQuests plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @Override
    public String getName() {
        return "startfight";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getSyntax() {
        return List.of("startfight");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return switch (args.length) {
            case 1 -> Arrays.stream(Bosses.values()).map(Bosses::toString).toList();
            case 2 -> null;
            default -> new ArrayList<>();
        };
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        Player target;
        if (args.length > 1)
            target = Bukkit.getPlayer(args[1]);
        else {
            if (!(sender instanceof Player player)) {
                Msgs.of("<red>You must be a player to do this!").send(sender);
                return true;
            }

            target = player;
        }

        User user = userManager.getUser(target);
        if (user == null) {
            Msgs.of("<red>An error occurred, please try again in a minute.").send(sender);
            return true;
        }

        Bosses boss = Bosses.getEnum(args[0]);
        if (user == null || boss == null)
            return false;

        plugin.getFightHandler().activate(boss, user);
        return true;
    }
}
