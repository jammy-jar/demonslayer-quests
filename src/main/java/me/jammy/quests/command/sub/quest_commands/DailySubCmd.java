package me.jammy.quests.command.sub.quest_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.QuestSubCommand;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.menu.quest_gui.DailyMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.entity.Player;

public class DailySubCmd extends QuestSubCommand {
    private final OasisQuests plugin;
    private final UserManager userManager;

    public DailySubCmd(OasisQuests plugin) {
        super(QuestType.DAILY.toString(), "");
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean run(final Player player, final String[] args) {
        User user = userManager.getUser(player);
        if (user == null) {
            Msgs.of("<red>An error occurred, please try again in a minute.").send(player);
            return true;
        }

        if (args.length == 0) {
            new DailyMenu(user).open();
            return true;
        }

        if (args.length == 1)
            return false;

        Quest quest = plugin.getQuestManager().getQuest(Quests.getEnum(args[1]));
        if (quest == null)
            return false;

        if (args[0].equalsIgnoreCase("activate")) {
            if (user.getActiveDailys().size() >= user.getMaxOfQuestType(QuestType.DAILY)) {
                Msgs.of("<red>You have selected the maximum amount of Daily Quests!").send(player);
                return true;
            }

            if (plugin.getQuestManager().todaysDailys().stream().noneMatch(daily -> daily.getQuest() == quest.getQuest())) {
                Msgs.of("<red>This Quest is not activated!").send(player);
                return true;
            }
            if (user.isQuestActive(quest)) {
                Msgs.of("<red>This Quest is already selected!").send(player);
                return true;
            }

            if (plugin.getQuestManager().todaysDailys().stream().anyMatch(daily -> daily.getQuest() == quest.getQuest())) {
                Msgs.of("<green>Activated Quest: " + quest.getDisplayName()).send(player);
                user.setActiveQuest(quest, true);
            } else {
                Msgs.of("<red>This Daily isn't available today!").send(player);
            }

            return true;
        } else if (args[0].equalsIgnoreCase("deactivate")) {
            Msgs.of("<red>You cannot deactivate daily quests!").send(player);
            return true;
        }

        return false;
    }
}
