package me.jammy.quests.menu.quest_gui;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.pagination.PaginationManager;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.menu.MenuParser;
import me.jammy.quests.menu.QuestGui;
import me.jammy.quests.menu.QuestMenu;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CampaignMenu extends QuestGui {

    private final PaginationManager paginationManager = new PaginationManager(this);
    private final OasisQuests plugin = OasisQuests.instance();
    private final QuestManager questManager = plugin.getQuestManager();
    private final RewardManager rewardManager = plugin.getRewardManager();

    private final User user;

    public CampaignMenu(@NotNull final User user) {
        super(user.getPlayer(), QuestType.CAMPAIGN);
        paginationManager.registerPageSlotsBetween(9, 17);

        this.user = user;
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>Back").build()).onClick(e -> new QuestMenu(player).open());
        addItem(18, backIcon);

        for (Campaign campaign : questManager.campaigns()) {
            RequirementType bossRequirement = campaign.getBoss().getBossRequirement();
            int bossProgress = user.getProgress(bossRequirement);
            boolean complete = bossProgress != 0;
            List<RequirementType> requirements = RequirementType.of(campaign.getQuest());
            requirements = requirements.stream().filter(r -> r != bossRequirement).toList();
            List<Progress> progress = user.getProgress(campaign.getQuest());
            progress = progress.stream().filter(p -> p.getRequirement() != bossRequirement).toList();
            Reward reward = rewardManager.get(campaign);

            ItemBuilder iconBuilder = ItemBuilder.start(campaign.getIcon())
                    .name(campaign.getDisplayName())
                    .lore(campaign.getBoss().getDescription());

            boolean active = user.isQuestActive(campaign);

            long cooldown = plugin.getCooldownManager().get(user.getPlayer().getUniqueId().toString(), campaign.getBoss());
            String cooldownString = plugin.getCooldownManager().getString(user.getPlayer().getUniqueId().toString(), campaign.getBoss());

            NPC npc = CitizensAPI.getNPCRegistry().getById(campaign.getNpc());
            Location location = npc.getStoredLocation();

            if (complete) {
                iconBuilder
                        .lore("")
                        .lore("<red><b>DEFEATED");
            } else if (!active) {
                iconBuilder
                        .lore("")
                        .lore("<red>Defeat the Previous Boss First!");
            } else {
                iconBuilder
                        .lore("<green>Visit <yellow>" + npc.getName() + " <green>NPC, at <yellow>"
                                + "x: " + location.getBlockX()  + " y: " + location.getBlockY() + " z: " + location.getBlockZ()
                                + " <green>in the <yellow>Quest <green>world to begin this fight!")
                        .lore("<dark_purple>This fight will cost " + MenuParser.fightCost(campaign.getBoss()) + "XP to begin! ")
                        .lore("<yellow>Current XP: " + player.getTotalExperience());

                if (cooldown > 0)
                    iconBuilder.lore("<yellow>Cooldown: " + cooldownString);

                iconBuilder
                        .lore("")
                        .lore(requirements.stream().map(r -> r.checkComplete(user) ? r.getDescription().color(TextColor.fromHexString("#55FF55")) : r.getDescription()).toList())
                        .lore("")
                        .lore("<gray>Progress: ")
                        .oldLore(progress.stream().map(p -> " <dark_purple>- " + (p.getAmount() == p.getRequirement().getAmount() ? "<b><green>" : "<yellow>")
                                + p.getAmount() + " / " + p.getRequirement().getAmount()
                                + " " + p.getRequirement().getUnit()).toList())
                        .lore("")
                        .lore("<gray>Next Reward:");
                iconBuilder.oldLore(reward.toStringList());
            }

            paginationManager.addItem(new Icon(iconBuilder.build()).onClick(click ->
                    Msgs.of("<green>Visit <yellow>" + npc.getName() + " <green>NPC, at <yellow>"
                            + "x: " + location.getBlockX()  + " y: " + location.getBlockY() + " z: " + location.getBlockZ()
                            + " <green>in the <yellow>Quest <green>world to begin this fight!").send(player)));
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
