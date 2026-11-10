package me.jammy.quests.model.user;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.angeschossen.lands.api.land.Area;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.progress.LevellableProgress;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.quest.daily.Daily;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.quest.special.Special;
import me.jammy.quests.model.requirement.RequirementClassification;
import me.jammy.quests.model.requirement.RequirementType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class User {
    private final OasisQuests plugin = OasisQuests.instance();
    private final QuestManager questManager = plugin.getQuestManager();

    private final Player player;

    private final Map<RequirementType, Progress> progress;
    private final Set<Quests> activeQuests;
    private final Set<Quests> specials;
    private boolean frozen;

    public User(Player player, final Map<RequirementType, Progress> progress, Set<Quests> activeQuests, Set<Quests> specials) {
        this.player = player;
        this.progress = progress;
        this.activeQuests = activeQuests;
        this.specials = specials;
        this.frozen = false;
    }

    public Player getPlayer() {
        return player;
    }

    public void action(RequirementType requirementType, int amount) {
        Quest quest = questManager.getQuest(requirementType.getQuest());
        if (!isQuestActive(quest))
            return;

        if (requirementType.getQuest().getQuestType() == QuestType.DAILY)
            if (!((Daily) quest).isQuestRunning()) return;

        if (getProgress(requirementType) == -1)
            return;
        addProgress(requirementType, amount);
        if (requirementType.getClassification() == RequirementClassification.QUANTIFIABLE) {
            if (getProgress(requirementType) >= requirementType.getAmount()) {
                setProgress(requirementType, requirementType.getAmount());
                quest.checkComplete(this);
            }
        } else if (requirementType.getClassification() == RequirementClassification.LEVELLABLE) {
            if (getProgress(requirementType) >= requirementType.getAmount(getLevel((Mission) quest))) {
                setProgress(requirementType, requirementType.getAmount(getLevel((Mission) quest)));
                quest.checkComplete(this);
            }
        }
    }

    public void action(RequirementType requirementType) {
        action(requirementType, 1);
    }

    public int getLevel(Mission mission) {
        return ((LevellableProgress) progress.get(RequirementType.getEnum(mission.getQuest().toString())))
                .getLevel();
    }

    public void addLevel(Mission mission) {
        ((LevellableProgress) progress.get(RequirementType.getEnum(mission.getQuest().toString())))
                .setLevel(getLevel(mission) + 1);
    }

    public void setLevel(Mission mission, int newLevel) {
        ((LevellableProgress) progress.get(RequirementType.getEnum(mission.getQuest().toString())))
                .setLevel(newLevel);
    }

    public void setAllLevels(int newLevel) {
        progress.values().stream()
                .filter(p -> p.getRequirement().getClassification() == RequirementClassification.LEVELLABLE)
                .forEach(p -> ((LevellableProgress) p).setLevel(newLevel));
    }

    public int getProgress(RequirementType requirement) {
        return progress.get(requirement).getAmount();
    }

    public List<Progress> getProgress(Quests quest) {
        return progress.values().stream()
                .filter(value -> value.getRequirement().getQuest() == quest)
                .toList();
    }

    public void setProgress(RequirementType requirement, int progress) {
        this.progress.get(requirement).setAmount(progress);
    }

    public void addProgress(RequirementType requirement, int progress) {
        this.progress.get(requirement).setAmount(getProgress(requirement) + progress);
    }

    public void setAllProgress(Quests quest, int progress) {
        this.progress.values().stream().filter(value -> value.getRequirement().getQuest() == quest)
                .forEach(value -> value.setAmount(progress));
    }

    public Collection<Progress> getProgressCollection() {
        return progress.values();
    }

    public void setActiveQuest(Quest quest, boolean b) {
        if (b) {
            activeQuests.add(quest.getQuest());
        } else {
            activeQuests.remove(quest.getQuest());
        }
    }

    public boolean isQuestActive(Quest quest) {
        if (quest.getQuestType() == QuestType.STARTER)
            return true;
        if (quest.getQuestType() == QuestType.CAMPAIGN) {
            List<Quests> sortedCampaigns = Arrays.stream(Quests.values()).sorted().filter(q -> q.getQuestType() == QuestType.CAMPAIGN).toList();
            if (quest.getQuest() == Quests.KYOGAI)
                return true;
            Campaign previous = questManager.getCampaign(sortedCampaigns.get(sortedCampaigns.indexOf(quest.getQuest()) - 1));
            return previous.isCompleted(this);
        }
        return activeQuests.contains(quest.getQuest());
    }

    public int getActiveQuestCount(QuestType questType) {
        return activeQuests.stream()
                .filter(quest -> questManager.getQuest(quest).getQuestType() == questType)
                .toList()
                .size();
    }

    public Set<Quests> getActiveQuests() {
        return activeQuests;
    }

    public List<Mission> getActiveMissions() {
        return activeQuests.stream()
                .filter(quest -> questManager.getQuest(quest).getQuestType() == QuestType.MISSION)
                .map(questManager::getMission)
                .toList();
    }

    public List<Daily> getActiveDailys() {
        return activeQuests.stream()
                .filter(quest -> questManager.getQuest(quest).getQuestType() == QuestType.DAILY)
                .map(questManager::getDaily)
                .toList();
    }

    public List<Special> getActiveSpecials() {
        return activeQuests.stream()
                .filter(quest -> questManager.getQuest(quest).getQuestType() == QuestType.SPECIAL)
                .map(questManager::getSpecial)
                .toList();
    }

    public boolean isSpecialOwned(Special special) {
        return specials.contains(special.getQuest());
    }

    public List<Special> getSpecials() {
        return specials.stream().map(quest -> plugin.getQuestManager().getSpecial(quest)).toList();
    }

    public void addSpecial(Special special) {
        specials.add(special.getQuest());
    }

    public void removeSpecial(Special special) {
        specials.remove(special.getQuest());
    }

    public void resetDailyProgress() {
        this.progress.values().stream().filter(progress -> progress.getRequirement().getQuest().getQuestType() == QuestType.DAILY)
                .forEach(progress -> progress.setAmount(0));
    }

    public int getMaxOfQuestType(QuestType questType) {
        List<PermissionAttachmentInfo> permissions = player.getEffectivePermissions().stream()
                .filter(permission -> permission.getPermission().startsWith("quests." + questType.toString() + "s.limit."))
                .filter(permission -> getActiveDailys().size() < parse(permission.getPermission().substring(permission.getPermission().lastIndexOf('.') + 1)))
                .toList();
        return permissions.stream()
                .map(perm -> parse(perm.getPermission().substring(perm.getPermission().lastIndexOf('.') + 1)))
                .max(Integer::compare)
                .orElse(-1);
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public boolean isInWarzone() {
        return player.getWorld().getName().equalsIgnoreCase("spawn_world");
    }

    public boolean isInQuestWorld() {
        return player.getWorld().getName().equalsIgnoreCase("quest_world");
    }

    public boolean inForeignClaim() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        RegionManager regions = container.get(wgPlayer.getWorld());
        int regionCount = regions.getRegionCountOfPlayer(wgPlayer);
        if (regionCount > 0)
            return true;

        if (OasisQuests.instance().getServer().getPluginManager().getPlugin("Lands") == null)
            return false;

        Location loc = player.getLocation();
        Area claim = plugin.getLandsIntegration().getArea(loc);
        if (claim == null)
            return false;

        return !claim.isTrusted(player.getUniqueId());
    }

    private int parse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NullPointerException ex) {
            return 0;
        }
    }
}
