package me.jammy.quests.menu.quest_gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.menu.QuestGui;
import me.jammy.quests.menu.QuestMenu;
import me.jammy.quests.menu.mission.MissionDetailsMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MissionMenu extends QuestGui {

    private final PaginationManager paginationManager = new PaginationManager(this);
    private final OasisQuests plugin = OasisQuests.instance();
    private final QuestManager questManager = plugin.getQuestManager();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;

    public MissionMenu(@NotNull final User user) {
        super(user.getPlayer(), QuestType.MISSION);
        paginationManager.registerPageSlotsBetween(0, 17);

        this.user = user;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new QuestMenu(player).open());
        addItem(18, backIcon);

        for (Mission mission : questManager.missions()) {
            int level = user.getLevel(mission);
            int progress = user.getProgress(RequirementType.getEnum(mission.getQuest().toString()));
            RequirementType requirement = RequirementType.getEnum(mission.getQuest().toString());
            boolean active = user.isQuestActive(mission);
            Reward reward = rewardManager.get(mission, level);

            ItemBuilder iconBuilder = ItemBuilder.start(mission.getIcon())
                    .name(mission.getDisplayName())
                    .lore(requirement.getDescription())
                    .lore("")
                    .lore("<gray>Current Tier: <yellow>" + level)
                    .lore("<gray>Progress: " + (progress == requirement.getAmount(level) ? "<b><green>" : "<yellow>") + progress + " / " + requirement.getAmount(level) + " " + requirement.getUnit())
                    .lore("")
                    .lore(active ? "<green>ACTIVE" : "<red>NOT ACTIVE");

            if (reward != null) {
                iconBuilder
                        .lore("")
                        .lore("<gray>Next Reward:");
                iconBuilder.oldLore(reward.toStringList());
            }

            paginationManager.addItem(new Icon(iconBuilder.build()).onClick(click -> new MissionDetailsMenu(user, mission).open()));
        }

        paginationManager.update();

        if (paginationManager.getLastPage() > 0) {
            ItemStack nextItem = InvIcon.NEXT.asBuilder()
                    .name("<gray>Next Page")
                    .build();
            Icon saveIcon = new Icon(nextItem).onClick(e -> paginationManager.goNextPage().update());
            addItem(23, saveIcon);

            ItemStack prevItem = InvIcon.PREV.asBuilder()
                    .name("<gray>Previous Page")
                    .build();
            saveIcon = new Icon(prevItem).onClick(e -> paginationManager.goPreviousPage().update());
            addItem(21, saveIcon);
        }
    }
}
