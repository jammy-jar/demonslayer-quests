package me.jammy.quests.menu.quest_gui;

import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.QuestGui;
import me.jammy.quests.menu.QuestMenu;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.daily.Daily;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DailyMenu extends QuestGui {

    private final OasisQuests plugin = OasisQuests.instance();
    private final QuestManager questManager = plugin.getQuestManager();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;

    public DailyMenu(@NotNull final User user) {
        super(user.getPlayer(), QuestType.DAILY);

        this.user = user;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new QuestMenu(player).open());
        addItem(18, backIcon);

        int activeDailys = user.getActiveQuestCount(QuestType.DAILY);
        int max = user.getMaxOfQuestType(QuestType.DAILY);

        ItemStack infoIcon = ItemBuilder.start(Material.PAPER).name("<gold>Daily Quest Info")
                .lore(max == -1 ? "<gray>You cannot select any more Dailys." : "<gray>You can select up to <gold>" + max + " <gray>Dailys",
                        "<yellow>WARNING: Once you have selected a daily quest you can not deselect it!")
                .build();
        addItem(22, new Icon(infoIcon));

        List<Daily> todaysDailys = questManager.todaysDailys();
        for (int i = 0; i < todaysDailys.size(); i++) {
            Daily daily = todaysDailys.get(i);
            List<Progress> progress = user.getProgress(daily.getQuest());
            List<RequirementType> requirements = RequirementType.of(daily.getQuest());
            boolean active = user.isQuestActive(daily);
            Reward reward = rewardManager.get(daily);

            boolean complete = progress.stream().allMatch(p -> p.getAmount() == -1);

            String status;
            if (active)
                status = "<green>SELECTED";
            else {
                if (activeDailys >= max)
                    status = "<dark_red>MAX SELECTED!";
                else
                    status = "<gray><i>Click to SELECT!";
            }

            ItemBuilder iconBuilder = ItemBuilder.start(daily.getIcon())
                    .name(daily.getDisplayName());

            if (complete) {
                iconBuilder
                        .lore("", "<green>COMPLETE");
            } else {
                iconBuilder
                        .lore(requirements.stream().map(r -> r.checkComplete(user) ? r.getDescription().color(TextColor.fromHexString("#55FF55")) : r.getDescription()).toList())
                        .lore("", "<gray>Progress: ")
                        .oldLore(progress.stream().map(p -> " <dark_purple>- " + (p.getAmount() == p.getRequirement().getAmount() ? "<b><green>" : "<yellow>")
                                + p.getAmount() + " / " + p.getRequirement().getAmount()
                                + " " + p.getRequirement().getUnit()).toList())
                        .lore("",
                                status,
                                "",
                                "<gray>Reward:")
                        .oldLore(reward.toStringList());
            }

            addItem(10 + (3 * i), new Icon(iconBuilder.build()).onClick(click -> {
                if (complete) {
                    Msgs.of("<red>This Quest is already complete!").send(player);
                    return;
                }
                if (!active && activeDailys >= max) {
                    Msgs.of("<red>You cannot select any more dailys!").send(player);
                    return;
                } else if (active) {
                    Msgs.of("<red>This daily quest is already selected!").send(player);
                    return;
                }
                user.setActiveQuest(daily, true);
                new DailyMenu(user).open();
            }));
        }
    }
}
