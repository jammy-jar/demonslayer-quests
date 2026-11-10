package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.quest_gui.*;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class QuestMenu extends Gui {

    private final User user;

    public QuestMenu(@NotNull final Player player) {
        super(player, "quest_menu", Msgs.of("<#69F2FF><b>Quests").asLegacy(), 3);

        OasisQuests plugin = OasisQuests.instance();
        UserManager userManager = plugin.getUserManager();
        this.user = userManager.getUser(player);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui((ItemBuilder.filler(DyeColor.GRAY).build()));

        Icon starterIcon = new Icon(QuestType.STARTER.getIcon())
                .onClick(e -> new StarterMenu(user).open());
        addItem(starterIcon, 10);
        Icon campaignIcon = new Icon(QuestType.CAMPAIGN.getIcon())
                .onClick(e -> new CampaignMenu(user).open());
        addItem(campaignIcon, 12);
        Icon dailyIcon = new Icon(QuestType.DAILY.getIcon())
                .onClick(e -> new DailyMenu(user).open());
        addItem(dailyIcon, 14);
        Icon specialsIcon = new Icon(QuestType.SPECIAL.getIcon())
                .onClick(e -> new SpecialsMenu(user).open());
        addItem(specialsIcon, 15);
        Icon missionIcon = new Icon(QuestType.MISSION.getIcon())
                .onClick(e -> new MissionMenu(user).open());
        addItem(missionIcon, 16);
    }
}
