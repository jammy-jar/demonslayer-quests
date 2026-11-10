package me.jammy.quests.command.sub.quest_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.QuestSubCommand;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.menu.quest_gui.MissionMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.entity.Player;

public class MissionSubCmd extends QuestSubCommand {
    private final OasisQuests plugin;
    private final UserManager userManager;

    public MissionSubCmd(OasisQuests plugin) {
        super(QuestType.MISSION.toString(), "");
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
            new MissionMenu(user).open();
            return true;
        }

        if (args.length == 1)
            return false;

        Quest quest = plugin.getQuestManager().getQuest(Quests.getEnum(args[1]));
        if (quest == null)
            return false;

        if (args[0].equalsIgnoreCase("activate")) {
            if (user.getActiveMissions().size() >= user.getMaxOfQuestType(QuestType.MISSION)) {
                Msgs.of("<red>You have selected the maximum amount of Missions, you must disable another first!").send(player);
                return true;
            }
            if (user.isQuestActive(quest)) {
                Msgs.of("<red>This Quest is already active!").send(player);
                return true;
            }

            Msgs.of("<green>Activated Quest: " + quest.getDisplayName()).send(player);
            user.setActiveQuest(quest, true);
            return true;
        } else if (args[0].equalsIgnoreCase("deactivate")) {
            if (!user.isQuestActive(quest)) {
                Msgs.of("<red>This Quest is not activated!").send(player);
                return true;
            }
            Msgs.of("<red>Deactivated Quest: " + quest.getDisplayName()).send(player);
            user.setActiveQuest(quest, false);
            return true;
        }
        return false;
    }
}
