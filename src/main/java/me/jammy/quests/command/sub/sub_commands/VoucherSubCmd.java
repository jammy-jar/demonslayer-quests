package me.jammy.quests.command.sub.sub_commands;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.command.sub.SubCommand;
import me.jammy.quests.model.item.CustomItemManager;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.util.config.Msgs;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoucherSubCmd extends SubCommand {
    private final QuestManager questManager;
    private final CustomItemManager itemManager;

    public VoucherSubCmd(OasisQuests plugin) {
        this.questManager = plugin.getQuestManager();
        this.itemManager = plugin.getCustomItemManager();
    }

    @Override
    public String getName() {
        return "voucher";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public List<String> getSyntax() {
        return List.of("voucher give <player> <special-quest> <amount>");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.admin";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return switch (args.length) {
            case 1 -> List.of("give");
            case 2 -> null;
            case 3 -> questManager.specials().stream().map(quest -> quest.getQuest().toString()).toList();
            default -> new ArrayList<>();
        };
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        if (args.length < 3)
            return false;

        int amount = 1;
        if (args.length > 3)
            amount = parse(args[3]);

        Player player = Bukkit.getPlayer(args[1]);
        if (player == null)
            return false;
        if (questManager.getSpecial(Quests.getEnum(args[2])) == null)
            return false;

        ItemStack voucher = itemManager.getSpecialVoucher(args[2]);
        voucher.setAmount(amount);
        player.getInventory().addItem(voucher);
        Msgs.of("<green>You received a <light_purple>" + PlainTextComponentSerializer.plainText().serialize(voucher.displayName())).send(player);

        return true;
    }

    private int parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NullPointerException ex) {
            return 1;
        }
    }
}
