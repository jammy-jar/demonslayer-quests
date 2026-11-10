package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetTierSubCmd extends SubCommand {

    private final OasisQuests plugin;
    private final UserManager userManager;

    public SetTierSubCmd(OasisQuests plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @Override
    public String getName() {
        return "settier";
    }

    @Override
    public String getDescription() {
        return "Set the tier of a users missions.";
    }

    @Override
    public List<String> getSyntax() {
        return List.of("settier <player> <mission> <tier>");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return switch (args.length) {
            case 1 -> null;
            case 2 -> {
                List<String> arguments = Arrays.stream(Quests.values()).filter(q -> q.getQuestType() == QuestType.MISSION).map(Quests::toString).collect(Collectors.toList());
                arguments.add("all");
                yield arguments;
            }
            case 3 -> List.of("1", "2", "3", "4", "5");
            default -> List.of();
        };
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        if (args.length < 3)
            return false;

        Player player = plugin.getServer().getPlayer(args[0]);
        if (player == null) {
            Msgs.of("<red>Error: " + args[0] + " is not a player on this server!").send(sender);
            return true;
        }
        User user = userManager.getUser(player);
        if (user == null) {
            Msgs.of("<red>An error occurred, please try again in a minute.").send(sender);
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            Msgs.of("<red>Error: " + args[2] + " is not a valid number!").send(sender);
            return true;
        }

        if (args[1].equalsIgnoreCase("all")) {
            user.setAllLevels(level);
            Msgs.of("<green>Successfully set all Mission Quests to tier " + level + " for player " + player.getName() + "!").send(sender);
            return true;
        }

        Quests quest = Quests.getEnum(args[1]);
        if (quest == null) {
            Msgs.of("<red>Error: " + args[1] + " is not a valid quest!").send(sender);
            return true;
        }
        Mission mission = plugin.getQuestManager().getMission(quest);
        user.setLevel(mission, level);
        Msgs.of("<green>Successfully set " + mission.getDisplayName() + " Mission to tier " + level + " for player " + player.getName() + "!").send(sender);
        return true;
    }
}
