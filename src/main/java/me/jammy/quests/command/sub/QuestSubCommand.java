package me.jammy.quests.command.sub;

import com.google.common.collect.Lists;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class QuestSubCommand extends SubCommand {
    private final OasisQuests plugin = OasisQuests.instance();

    private final String name;
    private final String desc;

    public QuestSubCommand(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    @Override
    public List<String> getSyntax() {
        return List.of(
                name,
                name + " activate <quest>",
                name + " deactivate <quest>");
    }

    @Override
    public String getRequiredPermission() {
        return "quests.default";
    }

    @Override
    public List<String> getArguments(final String[] args) {
        return switch (args.length) {
            case 1 -> Lists.newArrayList("activate", "deactivate");
            case 2 -> {
                QuestType questType = getQuestType();
                if (questType == null)
                    yield List.of();
                yield Arrays.stream(Quests.values()).filter(q -> q.getQuestType() == questType).map(Quests::toString).toList();
            }
            default -> List.of();
        };
    }

    private QuestType getQuestType() {
        try {
            return QuestType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public boolean run(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player player)) {
            Msgs.of("<red>You must be a player to do this!").send(sender);
            return true;
        }
        return run(player, args);
    }

    protected abstract boolean run(final Player player, final String[] args);
}
