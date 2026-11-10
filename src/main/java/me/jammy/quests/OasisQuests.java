package me.jammy.quests;

import com.google.common.collect.Maps;
import lombok.Getter;
import mc.obliviate.inventory.InventoryAPI;
import me.angeschossen.lands.api.LandsIntegration;
import me.jammy.quests.command.QuestsCmd;
import me.jammy.quests.conf.NpcVisitsConf;
import me.jammy.quests.conf.UniqueKillsConf;
import me.jammy.quests.listeners.*;
import me.jammy.quests.model.boss.BossManager;
import me.jammy.quests.model.boss.BossSummoner;
import me.jammy.quests.model.boss.FightHandler;
import me.jammy.quests.model.boss.WaitingHandler;
import me.jammy.quests.model.cooldown.CooldownManager;
import me.jammy.quests.model.item.CustomItemManager;
import me.jammy.quests.model.kit.KitHandler;
import me.jammy.quests.model.kit.KitManager;
import me.jammy.quests.model.miniboss.MiniBossManager;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.reward.RewardManager;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.util.QuestExpansion;
import me.jammy.quests.util.config.Cfgs;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;


public final class OasisQuests extends JavaPlugin {

    @Getter
    private static LuckPerms luckPerms = null;
    @Getter
    private static OasisQuests instance = null;
    @Getter
    private static Economy economy = null;
    @Getter
    private QuestManager questManager;
    @Getter
    private UserManager userManager;
    @Getter
    private RewardManager rewardManager;
    @Getter
    private CustomItemManager customItemManager;
    @Getter
    private CoreProtectAPI coreProtect;
    @Getter
    private BossSummoner bossSummoner;
    @Getter
    private MiniBossManager miniBossManager;
    @Getter
    private FightHandler fightHandler;
    @Getter
    private CooldownManager cooldownManager;
    @Getter
    private KitHandler kitHandler;
    @Getter
    private KitManager kitManager;
    @Getter
    private BossManager bossManager;
    @Getter
    private UniqueKillsConf uniqueKillsConf;
    @Getter
    private NpcVisitsConf npcVisitsConf;
    @Getter
    private WaitingHandler waitingHandler;
    @Getter
    private LandsIntegration landsIntegration;

    @Getter
    private Cfgs kitCfg;
    @Getter
    private Cfgs bossCfg;
    @Getter
    private Cfgs locationsCfg;
    @Getter
    private Cfgs missionRequirementsCfg;
    @Getter
    private Cfgs dailyRequirementsCfg;
    @Getter
    private Cfgs specialRequirementsCfg;
    @Getter
    private Cfgs campaignRequirementsCfg;
    @Getter
    private Cfgs starterRequirementsCfg;
    @Getter
    private Cfgs itemsCfg;
    @Getter
    private Cfgs playerDataCfg;
    @Getter
    private Cfgs cooldownCfg;
    @Getter
    private Cfgs uniqueKillsCfg;
    @Getter
    private Cfgs npcVisitsCfg;
    @Getter
    private Cfgs specialRewardsCfg;
    @Getter
    private Cfgs dailyRewardsCfg;
    @Getter
    private Cfgs campaignRewardsCfg;
    @Getter
    private final Map<Quests, Cfgs> missionRewardCfgs = Maps.newHashMap();

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();
        return (economy != null);
    }

    private boolean setupLuckPerms() {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null)
            luckPerms = provider.getProvider();
        return (luckPerms != null);
    }

    public void setupDb() {
        try {
            DataAccess.initDb();
        } catch (Exception e) {
            Bukkit.getLogger().warning("Something went wrong when initialising database. Exception:\n" + e);
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void setupCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        if (!(plugin instanceof CoreProtect))
            return;

        CoreProtectAPI coreProtect = ((CoreProtect) plugin).getAPI();
        if (!coreProtect.isEnabled())
            return;

        if (coreProtect.APIVersion() < 9)
            return;

        this.coreProtect = coreProtect;
    }

    private void setupPlaceholderExpansion() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new QuestExpansion(this).register();
        }
    }

    private void setupInventoryAPI() {
        new InventoryAPI(this).init();
    }

    @Override
    public void onLoad() {
        instance = this;

        saveDefaultConfig();
        registerConfigs();

        this.questManager = new QuestManager(this);

        this.rewardManager = new RewardManager(this);
        this.customItemManager = new CustomItemManager(this);
        this.kitManager = new KitManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.bossManager = new BossManager(this);
        this.miniBossManager = new MiniBossManager(this);

        this.bossSummoner = new BossSummoner(this);
        this.kitHandler = new KitHandler(this);
        this.fightHandler = new FightHandler(this);
        this.waitingHandler = new WaitingHandler();

        this.landsIntegration = LandsIntegration.of(this);

        this.npcVisitsConf = new NpcVisitsConf(this);
        this.uniqueKillsConf = new UniqueKillsConf(this);
    }

    @Override
    public void onEnable() {
        setupEconomy();
        setupLuckPerms();
        setupCoreProtect();
        setupInventoryAPI();
        setupPlaceholderExpansion();
        setupDb();

        this.userManager = new UserManager();

        this.userManager.runUserUpdater();
        this.userManager.runPlaytime();

        // Run Scheduler
        this.questManager.runDailyScheduler();
        this.questManager.runCheckComplete();
        this.questManager.initializeQuests();

        this.waitingHandler.runWaitingScheduler();

        new PermissionListener(this).register();

        this.getServer().getPluginManager().registerEvents(new QuestListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(this), this);
        this.getServer().getPluginManager().registerEvents(new ItemUseListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BossListener(this), this);

        //noinspection DataFlowIssue
        this.getCommand("quests").setExecutor(new QuestsCmd(this));
    }

    @Override
    public void onDisable() {
        DataAccess.disconnect();
    }

    private void registerConfigs() {
        File bossConfigFile = new File(getDataFolder(), "bosses/boss.yml");
        if (!bossConfigFile.exists())
            saveResource("bosses/boss.yml", false);
        this.bossCfg = Cfgs.of(bossConfigFile);
        File locationsConfigFile = new File(getDataFolder(), "bosses/locations.yml");
        if (!locationsConfigFile.exists())
            saveResource("bosses/locations.yml", false);
        this.locationsCfg = Cfgs.of(locationsConfigFile);
        File missionConfigFile = new File(getDataFolder(), "requirements/mission-requirements.yml");
        if (!missionConfigFile.exists())
            saveResource("requirements/mission-requirements.yml", false);
        this.missionRequirementsCfg = Cfgs.of(missionConfigFile);
        File dailyConfigFile = new File(getDataFolder(), "requirements/daily-requirements.yml");
        if (!dailyConfigFile.exists())
            saveResource("requirements/daily-requirements.yml", false);
        this.dailyRequirementsCfg = Cfgs.of(dailyConfigFile);
        File specialConfigFile = new File(getDataFolder(), "requirements/special-requirements.yml");
        if (!specialConfigFile.exists())
            saveResource("requirements/special-requirements.yml", false);
        this.specialRequirementsCfg = Cfgs.of(specialConfigFile);
        File campaignConfigFile = new File(getDataFolder(), "requirements/campaign-requirements.yml");
        if (!campaignConfigFile.exists())
            saveResource("requirements/campaign-requirements.yml", false);
        this.campaignRequirementsCfg = Cfgs.of(campaignConfigFile);
        File starterConfigFile = new File(getDataFolder(), "requirements/starter-requirements.yml");
        if (!starterConfigFile.exists())
            saveResource("requirements/starter-requirements.yml", false);
        this.starterRequirementsCfg = Cfgs.of(starterConfigFile);

        File itemsConfigFile = new File(getDataFolder(), "items-data.yml");
        if (!itemsConfigFile.exists())
            saveResource("items-data.yml", false);
        this.itemsCfg = Cfgs.of(itemsConfigFile);
        File kitConfigFile = new File(getDataFolder(), "bosses/kits.yml");
        if (!kitConfigFile.exists())
            saveResource("bosses/kits.yml", false);
        this.kitCfg = Cfgs.of(kitConfigFile);

        File playerDataConfigFile = new File(getDataFolder(), "temp/temp-player-data.yml");
        if (!playerDataConfigFile.exists())
            saveResource("temp/temp-player-data.yml", false);
        this.playerDataCfg = Cfgs.of(playerDataConfigFile);
        File cooldownConfigFile = new File(getDataFolder(), "temp/temp-cooldown-data.yml");
        if (!cooldownConfigFile.exists())
            saveResource("temp/temp-cooldown-data.yml", false);
        this.cooldownCfg = Cfgs.of(cooldownConfigFile);
        File npcVisitsConfigFile = new File(getDataFolder(), "temp/temp-npc-visits.yml");
        if (!npcVisitsConfigFile.exists())
            saveResource("temp/temp-npc-visits.yml", false);
        this.npcVisitsCfg = Cfgs.of(npcVisitsConfigFile);
        File tempUniqueKillsConfigFile = new File(getDataFolder(), "temp/temp-unique-kills.yml");
        if (!tempUniqueKillsConfigFile.exists())
            saveResource("temp/temp-unique-kills.yml", false);
        this.uniqueKillsCfg = Cfgs.of(tempUniqueKillsConfigFile);

        File dailyRewardsConfigFile = new File(getDataFolder(), "rewards/" + QuestType.DAILY + "-rewards.yml");
        if (!dailyRewardsConfigFile.exists())
            saveResource("rewards/" + QuestType.DAILY + "-rewards.yml", false);
        this.dailyRewardsCfg = Cfgs.of(dailyRewardsConfigFile);
        File campaignRewardsConfigFile = new File(getDataFolder(), "rewards/" + QuestType.CAMPAIGN + "-rewards.yml");
        if (!campaignRewardsConfigFile.exists())
            saveResource("rewards/" + QuestType.CAMPAIGN + "-rewards.yml", false);
        this.campaignRewardsCfg = Cfgs.of(campaignRewardsConfigFile);
        File specialRewardsConfigFile = new File(getDataFolder(), "rewards/" + QuestType.SPECIAL + "-rewards.yml");
        if (!specialRewardsConfigFile.exists())
            saveResource("rewards/" + QuestType.SPECIAL + "-rewards.yml", false);
        this.specialRewardsCfg = Cfgs.of(specialRewardsConfigFile);

        List<Quests> missions = Quests.filterType(QuestType.MISSION);
        for (Quests mission : missions) {
            File missionRewardsConfigFile = new File(getDataFolder(), "rewards/" + QuestType.MISSION + "-rewards/" + mission + "-rewards.yml");
            if (!missionRewardsConfigFile.exists())
                saveResource("rewards/" + QuestType.MISSION + "-rewards/" + mission + "-rewards.yml", false);
            this.missionRewardCfgs.put(mission, Cfgs.of(missionRewardsConfigFile));
        }
    }

    public static OasisQuests instance() {
        return instance;
    }

}
