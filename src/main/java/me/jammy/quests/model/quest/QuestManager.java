package me.jammy.quests.model.quest;

import com.google.common.collect.Lists;
import me.jammy.quests.DataAccess;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.quest.campaign.impl.*;
import me.jammy.quests.model.quest.daily.Daily;
import me.jammy.quests.model.quest.daily.impl.*;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.model.quest.mission.impl.*;
import me.jammy.quests.model.quest.special.Special;
import me.jammy.quests.model.quest.special.impl.AdventurerMaster;
import me.jammy.quests.model.quest.special.impl.AugustSpecial;
import me.jammy.quests.model.quest.special.impl.SeasonThree;
import me.jammy.quests.model.quest.special.impl.Slayer;
import me.jammy.quests.model.quest.starter.Starter;
import me.jammy.quests.model.quest.starter.impl.MainStarter;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.reward.Reward;
import me.jammy.quests.model.user.User;
import me.jammy.quests.util.config.Msgs;
import me.jammy.quests.util.config.QuestDataCfg;
import net.luckperms.api.node.Node;
import org.bukkit.Axis;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class QuestManager {
    private final OasisQuests plugin;
    private final List<Daily> dailys = Lists.newArrayList();
    private final List<Mission> missions = Lists.newArrayList();
    private final List<Special> specials = Lists.newArrayList();
    private final List<Campaign> campaigns = Lists.newArrayList();
    private final List<Starter> starters = Lists.newArrayList();
    private final List<Daily> todaysDailys = Lists.newArrayList();


    public QuestManager(OasisQuests plugin) {
        this.plugin = plugin;

        registerMission(
                Adventurer::new, Alchemist::new, Breeding::new,
                Enchanter::new, Experience::new, Farmer::new,
                Fisher::new, Miner::new, Playtime::new,
                Slaughterer::new, Tamer::new, Woodcutter::new
        );
        registerDaily(
                BreedChickens::new, Brew::new, CollectXPFromOres::new, Fish::new, KillDemons::new,
                KillDemonsWithFist::new, KillDemonsWithBow::new, KillDemonsInWarzone::new, KillPlayers::new,
                KillPlayersInWarzone::new, Swim::new
        );
        registerSpecial(
                SeasonThree::new, AugustSpecial::new, AdventurerMaster::new, Slayer::new
        );
        registerCampaign(
                Kyogai::new, Rui::new, Mukago::new,
                Wakurabo::new, Rokuro::new, Enmu::new,
                Kaigaku::new, Gyutaro::new
        );
        registerStarters(
                MainStarter::new
        );
    }

    public List<Quest> quests() {
        List<Quest> quests = new ArrayList<>();
        quests.addAll(dailys);
        quests.addAll(missions);
        quests.addAll(specials);
        quests.addAll(campaigns);
        quests.addAll(starters);
        return quests;
    }

    public List<? extends Quest> quests(QuestType type) {
        return switch (type) {
            case MISSION -> missions;
            case DAILY -> dailys;
            case SPECIAL -> specials;
            case CAMPAIGN -> campaigns;
            case STARTER -> starters;
        };
    }

    public List<Mission> missions() {
        return missions;
    }

    public List<Daily> dailys() {
        return dailys;
    }

    public List<Special> specials() {
        return specials;
    }

    public List<Campaign> campaigns() {
        return campaigns;
    }

    public List<Starter> starters() {
        return starters;
    }

    public List<Daily> todaysDailys() {
        return todaysDailys;
    }

    public Quest getQuest(Quests quest) {
        List<Quest> quests = new ArrayList<>();
        quests.addAll(dailys);
        quests.addAll(missions);
        quests.addAll(specials);
        quests.addAll(campaigns);
        quests.addAll(starters);
        return quests
                .stream()
                .filter(questInstance -> questInstance.getQuest() == quest)
                .findFirst()
                .orElse(null);
    }

    public Mission getMission(Quests quest) {
        return missions
                .stream()
                .filter(questInstance -> questInstance.getQuest() == quest)
                .findFirst()
                .orElse(null);
    }

    public Daily getDaily(Quests quest) {
        return dailys
                .stream()
                .filter(questInstance -> questInstance.getQuest() == quest)
                .findFirst()
                .orElse(null);
    }

    public Special getSpecial(Quests quest) {
        return specials
                .stream()
                .filter(questInstance -> questInstance.getQuest() == quest)
                .findFirst()
                .orElse(null);
    }

    public Campaign getCampaign(Quests quest) {
        return campaigns
                .stream()
                .filter(questInstance -> questInstance.getQuest() == quest)
                .findFirst()
                .orElse(null);
    }

    public MainStarter getStarter() {
        return (MainStarter) starters.stream().findFirst().orElse(null);
    }

    @SafeVarargs
    private void registerMission(Supplier<Mission>... suppliers) {
        for (Supplier<Mission> supplier : suppliers)
            missions.add(supplier.get());
    }

    @SafeVarargs
    private void registerDaily(Supplier<Daily>... suppliers) {
        for (Supplier<Daily> supplier : suppliers)
            dailys.add(supplier.get());
    }

    @SafeVarargs
    private void registerSpecial(Supplier<Special>... suppliers) {
        for (Supplier<Special> supplier : suppliers)
            specials.add(supplier.get());
    }

    @SafeVarargs
    private void registerCampaign(Supplier<Campaign>... suppliers) {
        for (Supplier<Campaign> supplier : suppliers)
            campaigns.add(supplier.get());
    }

    @SafeVarargs
    private void registerStarters(Supplier<Starter>... suppliers) {
        for (Supplier<Starter> supplier : suppliers)
            starters.add(supplier.get());
    }

    public void initializeQuests() {
        for (Quest quest : quests())
            DataAccess.addQuest(quest.getQuest());

        DataAccess.setQuestIdMap();
    }

    public void complete(User user, Quest quest) {
        Reward reward = plugin.getRewardManager().get(quest);
        if (quest.getQuestType() == QuestType.MISSION)
            reward = plugin.getRewardManager().get(quest, user.getLevel((Mission) quest));
        Player player = user.getPlayer();

        if (quest.getQuestType() == QuestType.MISSION)
            missionLogic(user, (Mission) quest);
        if (quest.getQuestType() == QuestType.DAILY)
            dailyLogic(user, quest);
        if (quest.getQuestType() == QuestType.SPECIAL)
            specialLogic(user, quest);
        if (quest.getQuestType() == QuestType.CAMPAIGN)
            campaignLogic(user, quest);
        if (quest.getQuestType() == QuestType.STARTER)
            campaignLogic(user, quest);

        reward.giveReward(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        player.playEffect(player.getLocation(), Effect.ELECTRIC_SPARK, Axis.X);
    }

    private void campaignLogic(User user, Quest quest) {
        user.setAllProgress(quest.getQuest(), -1);
        Msgs.of(quest.getQuestType().getFormattedName() + " Quest Complete: " + quest.getDisplayName() + "!").send(user.getPlayer());
    }

    private void dailyLogic(User user, Quest quest) {
        user.setAllProgress(quest.getQuest(), -1);
        user.setActiveQuest(quest, false);
        Msgs.of(quest.getQuestType().getFormattedName() + " Quest Complete: " + quest.getDisplayName() + "!").send(user.getPlayer());
    }

    private void specialLogic(User user, Quest quest) {
        user.setAllProgress(quest.getQuest(), 0);
        user.setActiveQuest(quest, false);
        OasisQuests.getLuckPerms().getUserManager().modifyUser(user.getPlayer().getUniqueId(), player ->
                player.data().remove(Node.builder("quests.special." + quest.getQuest()).build()));
        Msgs.of(quest.getQuestType().getFormattedName() + " Quest Complete: " + quest.getDisplayName() + "!").send(user.getPlayer());
    }

    private void missionLogic(User user, Mission quest) {
        int level = user.getLevel(quest);
        user.setProgress(RequirementType.getEnum(quest.getQuest().toString()), 0);
        user.addLevel(quest);
        Msgs.of(quest.getQuestType().getFormattedName() + " Quest Level Up: <green>You just levelled up your <yellow>" + quest.getDisplayName() + "<green> mission to Tier " + (level + 1) + "!").send(user.getPlayer());
    }

    public void runDailyScheduler() {
        todaysDailys.addAll(QuestDataCfg.fetchTodaysDailys().stream().map(daily -> getDaily(Quests.getEnum(daily))).toList());
        if (todaysDailys.isEmpty())
            rerollQuests();
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextRun = now.withHour(0).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) > 0)
            nextRun = nextRun.plusDays(1);

        Duration duration = Duration.between(now, nextRun);
        long initialDelay = duration.getSeconds();

        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, this::rerollQuests, initialDelay * 20, TimeUnit.DAYS.toSeconds(1) * 20);
    }

    public void rerollQuests() {
        plugin.getUserManager().resetDailys();
        todaysDailys.clear();

        List<Daily> remainingDailys = Lists.newArrayList();
        remainingDailys.addAll(dailys);
        for (int i = 0; i < 3; i++) {
            int questNum = (int) (Math.random() * remainingDailys.size());
            todaysDailys.add(remainingDailys.remove(questNum));
        }
        QuestDataCfg.setTodaysDailys(todaysDailys.stream().map(quest -> quest.getQuest().toString()).toList());
    }

    public void runCheckComplete() {
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            for (User user : plugin.getUserManager().users()) {
                for (Quest quest : quests()) {
                    quest.checkComplete(user);
                }
            }
        }, 0, 300 * 20);
    }
}
