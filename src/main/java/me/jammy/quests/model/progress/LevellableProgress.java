package me.jammy.quests.model.progress;

import lombok.Getter;
import lombok.Setter;
import me.jammy.quests.model.requirement.RequirementType;

public class LevellableProgress extends Progress {
    @Getter
    @Setter
    private int level;

    public LevellableProgress(final RequirementType requirementType, final int amount, final int level) {
        super(requirementType, amount);
        this.level = level;
    }

    public LevellableProgress(final RequirementType requirementType) {
        this(requirementType, 0, 1);
    }
}
