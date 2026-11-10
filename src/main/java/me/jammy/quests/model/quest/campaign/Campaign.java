package me.jammy.quests.model.quest.campaign;

import lombok.Getter;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.boss.Bosses;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.util.config.Msgs;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Locale;

public abstract class Campaign extends Quest {
    private final OasisQuests plugin = OasisQuests.instance();

    @Getter
    private final Bosses boss;

    public Campaign(final QuestType questType, final Bosses boss, final Material icon) {
        super(questType, boss.getQuest(), boss.getDisplayName(), icon);
        this.boss = boss;
    }

    public int getNpc() {
        return plugin.getBossCfg().get().getInt(boss.name().toLowerCase(Locale.ROOT) + ".npc.id");
    }

    public void start(User user) {
        long cooldown = plugin.getCooldownManager().get(user.getPlayer().getUniqueId().toString(), boss);
        Player player = user.getPlayer();

        if (!checkRequirements(user)) {
            player.setVelocity(player.getLocation().getDirection().multiply(-1.5));
            Msgs.of("<red>You do not meet the requirements for this boss fight!").send(player);
            return;
        } else if (cooldown > 0) {
            player.setVelocity(player.getLocation().getDirection().multiply(-1.5));
            Msgs.of("<red>You must wait until the cool down is over before you can fight! Cooldown: " + cooldown).send(player);
            return;
        }

        plugin.getFightHandler().activate(boss, user);
    }

    private boolean checkRequirements(User user) {
        return RequirementType.of(getQuest()).stream().allMatch(r -> r == boss.getBossRequirement() || r.checkComplete(user));
    }

}
