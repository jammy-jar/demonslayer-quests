package me.jammy.quests.menu.mission;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.quest_gui.MissionMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class MissionDetailsMenu extends Gui {

    private final OasisQuests plugin = OasisQuests.instance();
    private final User user;
    private final Mission mission;

    public MissionDetailsMenu(User user, Mission mission) {
        super(user.getPlayer(), mission.getQuestType().toString() + "_details_menu", Msgs.of(mission.getQuestType().getFormattedName() + " Info").asLegacy(), 3);
        this.user = user;
        this.mission = mission;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new MissionMenu(user).open());
        addItem(18, backIcon);

        boolean active = user.isQuestActive(mission);
        int activeMissions = user.getActiveQuestCount(QuestType.MISSION);
        Icon activateIcon;
        if (active)
            activateIcon = new Icon(ItemBuilder.start(Material.RED_CONCRETE)
                    .name("<red>Deactivate")
                    .lore("<gray>Click to Deactivate!")
                    .build()
            ).onClick(click -> {
                user.setActiveQuest(mission, false);
                this.open();
            });
        else {
            int max = user.getMaxOfQuestType(QuestType.MISSION);
            if (activeMissions >= max)
                activateIcon = new Icon(ItemBuilder.start(Material.GRAY_CONCRETE)
                        .name("<dark_red>Max Active!")
                        .lore("<gray>You cannot activate any more missions!")
                        .lore("<gray>Deactivate another mission.")
                        .build()
                );
            else
                activateIcon = new Icon(ItemBuilder.start(Material.GREEN_CONCRETE)
                        .name("<green>Activate")
                        .lore("<gray>Click to Activate!")
                        .build()
                ).onClick(click -> {
                    user.setActiveQuest(mission, true);
                    this.open();
                });
        }
        addItem(11, activateIcon);

        int level = user.getLevel(mission);
        RequirementType requirementType = RequirementType.getEnum(mission.getQuest().toString());
        int progress = user.getProgress(requirementType);
        Reward reward = plugin.getRewardManager().get(mission, level);

        ItemBuilder iconBuilder = ItemBuilder.start(Material.FILLED_MAP)
                .name(mission.getDisplayName() + " Tiers")
                .lore("<gray><i>Click to see all tiers.")
                .lore("")
                .lore("<gray>Current Tier: <yellow>" + level)
                .lore("<gray>Progress: " + (progress == requirementType.getAmount(level) ? "<b><green>" : "<yellow>") + progress + " / " + requirementType.getAmount(level) + " " + requirementType.getUnit());

        if (reward != null) {
            iconBuilder
                    .lore("")
                    .lore("<gray>Next Reward:");
            iconBuilder.oldLore(reward.toStringList());
        }

        addItem(15, new Icon(iconBuilder.build()).onClick(click -> new TiersMenu(user, mission).open()));
    }
}
