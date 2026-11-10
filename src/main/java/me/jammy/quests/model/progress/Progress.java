package me.jammy.quests.model.progress;

import lombok.Getter;
import lombok.Setter;
import me.jammy.quests.model.requirement.RequirementType;

public class Progress {
    @Getter
    @Setter
    private RequirementType requirement;
    @Getter
    @Setter
    private int amount;

    public Progress(final RequirementType requirementType, final int amount) {
        this.requirement = requirementType;
        this.amount = amount;
    }

    public Progress(final RequirementType requirementType) {
        this(requirementType, 0);
    }
}
