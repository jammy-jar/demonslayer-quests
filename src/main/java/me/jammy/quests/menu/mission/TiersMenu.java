package me.jammy.quests.menu.mission;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.menu.quest_gui.MissionMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TiersMenu extends Gui {

    private final PaginationManager paginationManager = new PaginationManager(this);
    private final OasisQuests plugin = OasisQuests.instance();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;
    private final Mission mission;

    public TiersMenu(@NotNull final User user, @NotNull final Mission mission) {
        super(user.getPlayer(), "tiers_menu", Msgs.of(mission.getDisplayName() + " Tiers").asLegacy(), 4);
        this.user = user;
        this.mission = mission;

        List<Integer> levelSlots = List.of(0, 9, 18, 19, 20, 11, 2, 3, 4, 13, 22, 23, 24, 15, 6, 7, 8, 17, 26);
        paginationManager.registerPageSlots(levelSlots);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new MissionMenu(user).open());
        addItem(27, backIcon);

        int userLevel = user.getLevel(mission);

        RequirementType requirement = RequirementType.getEnum(mission.getQuest().toString());
        for (int i = 0; i < mission.getMaxLevel(); i++) {
            int level = i + 1;
            boolean unlocked = userLevel >= level;
            Reward reward = rewardManager.get(mission, level);

            boolean currentLevel = level == userLevel;

            ItemBuilder iconBuilder;
            if (currentLevel)
                iconBuilder = ItemBuilder.start(Material.GREEN_CONCRETE).glow().oldLore(
                        List.of("<gray>Progress:", " <gray>- <yellow>" + user.getProgress(RequirementType.getEnum(mission.getQuest().toString())) + " / " + requirement.getAmount(level) + " " + requirement.getUnit(), "")
                );
            else
                iconBuilder = ItemBuilder.start(unlocked ? Material.GREEN_CONCRETE : Material.GRAY_CONCRETE);

            iconBuilder
                    .name("<green>Tier " + level);

            if (reward != null) {
                iconBuilder
                        .lore("<gray>Reward:")
                        .oldLore(reward.toStringList());
            }

            paginationManager.addItem(new Icon(iconBuilder.build()));
        }

        paginationManager.update();

        if (paginationManager.getLastPage() > 0) {
            ItemStack nextItem = InvIcon.NEXT.asBuilder()
                    .name("<gray>Next Page")
                    .build();
            Icon saveIcon = new Icon(nextItem).onClick(e -> paginationManager.goNextPage().update());
            addItem(32, saveIcon);

            ItemStack prevItem = InvIcon.PREV.asBuilder()
                    .name("<gray>Previous Page")
                    .build();
            saveIcon = new Icon(prevItem).onClick(e -> paginationManager.goPreviousPage().update());
            addItem(30, saveIcon);
        }
    }
}
