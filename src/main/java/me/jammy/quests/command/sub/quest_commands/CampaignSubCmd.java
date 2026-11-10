package me.jammy.quests.command.sub.quest_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.QuestSubCommand;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.menu.quest_gui.CampaignMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.entity.Player;

public class CampaignSubCmd extends QuestSubCommand {
    private final UserManager userManager;

    public CampaignSubCmd(OasisQuests plugin) {
        super(QuestType.DAILY.toString(), "");
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean run(final Player player, final String[] args) {
        User user = userManager.getUser(player);
        if (user == null) {
            Msgs.of("<red>An error occurred, please try again in a minute.").send(player);
            return true;
        }

        new CampaignMenu(user).open();
        return true;
    }
}
