package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.menu.KitMenu;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class KitSubCmd extends SubCommand {

    private final QuestManager questManager;

    public KitSubCmd(final OasisQuests plugin) {
        this.questManager = plugin.getQuestManager();
    }

    @Override
    public String getName() {
        return "kit";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getSyntax() {
        return List.of("kit <boss>");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return switch (args.length) {
            case 1 -> questManager.campaigns().stream().map(quest -> quest.getQuest().toString()).toList();
            default -> new ArrayList<>();
        };
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player player)) {
            Msgs.of("<red>You must be a player to do this!").send(sender);
            return true;
        }
        if (args.length != 1)
            return false;

        if (Arrays.stream(Bosses.values()).noneMatch(boss -> boss.name().equalsIgnoreCase(args[0]))) {
            Msgs.of("<red>The following Boss: " + args[0] + ", does not exist!").send(sender);
            return true;
        }

        Bosses boss = Bosses.valueOf(args[0].toUpperCase(Locale.ROOT));
        new KitMenu(player, boss).open();
        return true;
    }
}
