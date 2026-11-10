package me.jammy.quests.menu.quest_gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.menu.QuestGui;
import me.jammy.quests.menu.QuestMenu;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.special.Special;
import me.jammy.quests.model.reward.Reward;
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

public class SpecialsMenu extends QuestGui {
    private final PaginationManager paginationManager = new PaginationManager(this);
    private final OasisQuests plugin = OasisQuests.instance();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;

    public SpecialsMenu(@NotNull final User user) {
        super(user.getPlayer(), QuestType.SPECIAL);
        this.user = user;
        paginationManager.registerPageSlotsBetween(0, 17);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new QuestMenu(player).open());
        addItem(18, backIcon);

        int activeSpecials = user.getActiveQuestCount(QuestType.SPECIAL);
        int max = user.getMaxOfQuestType(QuestType.SPECIAL);

        ItemStack infoIcon = ItemBuilder.start(Material.PAPER).name("<gold>Special Quest Info")
                .lore(max == -1 ? "<gray>You cannot activate any more Specials." : "<gray>You can activate up to <gold>" + max + " <gray>Specials")
                .build();
        addItem(22, new Icon(infoIcon));

        List<Special> specials = plugin.getQuestManager().specials();
        for (Special special : specials) {
            List<Progress> progress = user.getProgress(special.getQuest());
            List<RequirementType> requirements = RequirementType.of(special.getQuest());
            boolean active = user.isQuestActive(special);
            Reward reward = rewardManager.get(special);
            boolean isOwned = user.isSpecialOwned(special);

            String status;
            if (active)
                status = "<green>ACTIVATED";
            else if (!isOwned) {
                status = "<dark_red>LOCKED! You must purchase this from the store.";
            } else {
                if (activeSpecials >= max)
                    status = "<dark_red>MAX ACTIVE!";
                else
                    status = "<gray><i>Click to ACTIVATE!";
            }

            ItemBuilder iconBuilder = ItemBuilder.start(special.getIcon())
                    .name(special.getDisplayName());

            iconBuilder
                    .lore(requirements.stream().map(r -> r.checkComplete(user) ? r.getDescription().color(TextColor.fromHexString("#55FF55")) : r.getDescription()).toList())
                    .lore("", "<gray>Progress: ")
                    .oldLore(progress.stream().map(p -> " <dark_purple>- " + (p.getAmount() == p.getRequirement().getAmount() ? "<b><green>" : "<yellow>")
                            + p.getAmount() + " / " + p.getRequirement().getAmount()
                            + " " + p.getRequirement().getUnit()).toList());
            iconBuilder.lore("",
                            status,
                            "",
                            "<gray>Reward:")
                    .oldLore(reward.toStringList());

            paginationManager.addItem(new Icon(iconBuilder.build()).onClick(click -> {
                if (!isOwned) {
                    Msgs.of("<red>LOCKED! You must purchase this special quest from the <yellow>store.mc-oasis.com<red> to select this Special Quest!").send(player);
                    return;
                }
                if (!active && activeSpecials >= max) {
                    Msgs.of("<red>You cannot activate any more specials!").send(player);
                    return;
                }
                user.setActiveQuest(special, !active);
                new SpecialsMenu(user).open();
            }));
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
