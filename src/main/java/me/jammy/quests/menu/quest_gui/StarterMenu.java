package me.jammy.quests.menu.quest_gui;

import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.QuestGui;
import me.jammy.quests.menu.QuestMenu;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.starter.impl.MainStarter;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StarterMenu extends QuestGui {
    private final OasisQuests plugin = OasisQuests.instance();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;

    public StarterMenu(@NotNull final User user) {
        super(user.getPlayer(), QuestType.STARTER);
        this.user = user;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new QuestMenu(player).open());
        addItem(18, backIcon);

        MainStarter starter = plugin.getQuestManager().getStarter();
        List<Progress> progress = user.getProgress(starter.getQuest());
        List<RequirementType> requirements = RequirementType.of(starter.getQuest());
        Reward reward = rewardManager.get(starter);

        boolean complete = progress.stream().allMatch(p -> p.getAmount() == -1);

        ItemBuilder iconBuilder = ItemBuilder.start(starter.getIcon())
                .name(starter.getDisplayName());

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
                            "<gray>Reward:")
                    .oldLore(reward.toStringList());
        }

        addItem(13, new Icon(iconBuilder.build()).onClick(click -> {
            if (complete)
                Msgs.of("<red>This Quest is already complete!").send(player);
        }));
    }
}
