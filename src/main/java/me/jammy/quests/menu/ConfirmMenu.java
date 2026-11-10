package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import mc.obliviate.inventory.Icon;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.inventory.InvIcon;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class ConfirmMenu extends Gui {

    private final OasisQuests plugin = OasisQuests.instance();
    final User user;
    final Campaign campaign;

    public ConfirmMenu(@NotNull final Player player, Campaign campaign) {
        super(player, "confirm_fight_menu", Msgs.of("<gold>Would you like to begin this fight?").asLegacy(), 1);
        this.campaign = campaign;
        this.user = plugin.getUserManager().getUser(player);
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        fillGui(ItemBuilder.filler(DyeColor.GRAY).build());

        Icon backIcon = new Icon(InvIcon.BACK.asBuilder().name("<red>No").build()).onClick(e -> e.getInventory().close());
        addItem(2, backIcon);

        Icon confirmIcon = new Icon(InvIcon.CONFIRM.asBuilder().name("<green>Yes").build())
                .onClick(e -> {
                    if (!validate()) {
                        event.getInventory().close();
                        return;
                    }
                    Campaign quest = (Campaign) plugin.getQuestManager().getQuest(campaign.getQuest());
                    quest.start(user);
                    event.getInventory().close();
                });

        long cooldown = plugin.getCooldownManager().get(user.getPlayer().getUniqueId().toString(), campaign.getBoss());
        String cooldownString = plugin.getCooldownManager().getString(user.getPlayer().getUniqueId().toString(), campaign.getBoss());
        Icon waitIcon = new Icon(InvIcon.IMPORTANT.asBuilder().name("<yellow>Cooldown!")
                .lore("Please wait " + cooldownString + " before trying to start this fight!").build())
                .onClick(e -> {
                    Msgs.of("<red>You cannot start this fight as you have a cooldown active! Please wait " + cooldownString + " before trying again!");
                    event.getInventory().close();
                });
        addItem(6, cooldown > 0 ? waitIcon : confirmIcon);

        Icon infoIcon = new Icon(ItemBuilder
                .start(Material.PAPER)
                .name(campaign.getDisplayName())
                .lore("<yellow>WARNING: This fight will cost you " + MenuParser.fightCost(campaign.getBoss()) + "XP to start!")
                .build());
        addItem(4, infoIcon);
    }

    private boolean validate() {
        if (user.getProgress(campaign.getBoss().getBossRequirement()) != 0) {
            Msgs.of("<red>You have already defeated this boss!").send(player);
            return false;
        }

        int fightCost = MenuParser.fightCost(campaign.getBoss());

        if (player.getTotalExperience() < fightCost) {
            player.sendMessage(Msgs.of("<red>You must have " + fightCost + "XP to fight " + campaign.getBoss().getDisplayName()).asComp());
            return false;
        }

        player.setTotalExperience(player.getTotalExperience() - fightCost);
        return true;
    }
}
