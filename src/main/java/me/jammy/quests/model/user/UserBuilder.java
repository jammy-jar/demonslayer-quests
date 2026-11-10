package me.jammy.quests.model.user;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.jammy.quests.model.progress.LevellableProgress;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.requirement.RequirementClassification;
import me.jammy.quests.model.requirement.RequirementType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserBuilder {
    private final Player player;
    private final Map<RequirementType, Progress> progress = Maps.newHashMap();
    private final Set<Quests> activeQuests = Sets.newHashSet();
    private Set<Quests> specials = Sets.newHashSet();

    public UserBuilder(Player player) {
        this.player = player;

        initializeSpecials();
        initializeProgress();
    }

    private void initializeSpecials() {
        this.specials = new HashSet<>(specials);
        player.getEffectivePermissions().forEach(permission -> {
            if (permission.getPermission().startsWith("quests.special."))
                specials.add(Quests.getEnum(permission.getPermission().substring(permission.getPermission().lastIndexOf(".") + 1)));
        });
    }

    private void initializeProgress() {
        for (RequirementType requirementType : RequirementType.values()) {
            if (requirementType.getClassification() == RequirementClassification.BASIC
                    || requirementType.getClassification() == RequirementClassification.TIER
                    || requirementType.getClassification() == RequirementClassification.PERMISSION)
                continue;
            if (requirementType.getClassification() == RequirementClassification.LEVELLABLE) {
                progress.put(requirementType, new LevellableProgress(requirementType));
                continue;
            }
            progress.put(requirementType, new Progress(requirementType));
        }
    }

    public UserBuilder progress(List<Progress> progressList) {
        for (Progress progress : progressList)
            this.progress.put(progress.getRequirement(), progress);
        return this;
    }

    public UserBuilder activeQuests(Set<Quests> activeQuests) {
        this.activeQuests.addAll(activeQuests);
        return this;
    }

    public User build() {
        return new User(player, progress, activeQuests, specials);
    }
}
