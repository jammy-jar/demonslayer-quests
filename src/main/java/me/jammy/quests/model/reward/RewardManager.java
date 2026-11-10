package me.jammy.quests.model.reward;

import com.google.common.collect.Maps;
import com.udojava.evalex.Expression;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestType;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.quest.mission.Mission;
import me.jammy.quests.util.config.Cfgs;
import me.jammy.quests.util.inventory.ItemBuilder;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardManager {

    private final Map<String, Reward> rewards = Maps.newHashMap();
    private Map<String, ItemStack> items = Maps.newHashMap();

    private Cfgs itemsCfg;
    private final OasisQuests plugin;

    public RewardManager(OasisQuests plugin) {
        this.plugin = plugin;
        loadItems();

        List<Quests> missions = Quests.filterType(QuestType.MISSION);
        for (Quests mission : missions)
            loadMissionRewards(plugin.getMissionRewardCfgs().get(mission).get(), plugin.getQuestManager().getMission(mission));
        loadRewards(QuestType.DAILY);
        loadRewards(QuestType.SPECIAL);
        loadRewards(QuestType.CAMPAIGN);

        ItemStack nichirin = ItemBuilder.nichirin().build();
        nichirin.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        rewards.put(plugin.getQuestManager().getStarter().getQuest().toString(), new RewardBuilder()
                .items(List.of(nichirin))
                        .eco(5000)
                .build());
    }

    private void loadItems() {
        itemsCfg = plugin.getItemsCfg();
        for (String key : itemsCfg.get().getKeys(false)) {
            ItemStack value = itemsCfg.get().getItemStack(key);
            items.put(key, value);
        }
    }

    public void loadMissionRewards(Configuration config, Mission mission) {
        ConfigurationSection defaultSection = config.getConfigurationSection("default");

        int baseEco = defaultSection.getInt("eco");
        int baseExp = defaultSection.getInt("exp");
        Expression expression = new Expression(defaultSection.getString("expression"));
        for (int i = 0; i < mission.getMaxLevel() - 1; i++) {
            int level = i + 1;
            expression.setVariable("level", BigDecimal.valueOf(level));
            expression.setVariable("base", BigDecimal.valueOf(baseEco));
            int eco = expression.eval().intValue();
            expression.setVariable("base", BigDecimal.valueOf(baseExp));
            int exp = expression.eval().intValue();

            rewards.put(mission.getQuest().toString() + level, new RewardBuilder()
                    .level(level)
                    .eco(eco)
                    .exp(exp)
                    .build()
            );
        }

        for (String key : config.getKeys(false)) {
            if (!key.startsWith("level-"))
                continue;
            int level = Integer.parseInt(key.substring(key.indexOf("-") + 1));
            ConfigurationSection section = config.getConfigurationSection(key);
            registerReward(mission, section, level);
        }
    }

    public void loadRewards(QuestType questType) {
        Cfgs rewardsConfig = switch (questType) {
            case MISSION -> null;
            case SPECIAL -> plugin.getSpecialRewardsCfg();
            case CAMPAIGN -> plugin.getCampaignRewardsCfg();
            case DAILY -> plugin.getDailyRewardsCfg();
            case STARTER -> null;
        };

        for (String key : rewardsConfig.get().getKeys(false)) {
            Quest quest = plugin.getQuestManager().getQuest(Quests.getEnum(key));
            if (quest == null)
                return;
            registerReward(quest, rewardsConfig.get().getConfigurationSection(key), 0);
        }
    }

    private void registerReward(Quest quest, ConfigurationSection section, int level) {
        List<ItemStack> itemRewards = section.getStringList("items").stream()
                .map(id -> items.get(id)).toList();

        List<List<String>> commandRewards = new ArrayList<>();
        ConfigurationSection commandRewardsSection = section.getConfigurationSection("command-rewards");
        for (String key1 : commandRewardsSection.getKeys(false)) {
            ConfigurationSection commandSection = commandRewardsSection.getConfigurationSection(key1);
            commandRewards.add(List.of(
                    commandSection.getString("label"),
                    commandSection.getString("command")
            ));
        }

        int eco = section.getInt("eco");
        int exp = section.getInt("exp");

        if (level != 0) {
            rewards.put(quest.getQuest().toString() + level, new RewardBuilder()
                    .level(level)
                    .items(itemRewards)
                    .commands(commandRewards)
                    .eco(eco)
                    .exp(exp)
                    .build()
            );
        } else {
            rewards.put(quest.getQuest().toString(), new RewardBuilder()
                    .items(itemRewards)
                    .commands(commandRewards)
                    .eco(eco)
                    .exp(exp)
                    .build()
            );
        }
    }

    public void saveItems(Map<String, ItemStack> items, List<String> toRemove) {
        for (String key : toRemove) {
            itemsCfg.set(key, null);
        }

        this.items = items;
        for (String key : items.keySet()) {
            ItemStack item = items.get(key);
            itemsCfg.set(key, item);
        }
        itemsCfg.save();
    }

    public Reward get(Quest quest, int level) {
        return rewards.get(quest.getQuest().toString() + level);
    }

    public Reward get(Quest quest) {
        return rewards.get(quest.getQuest().toString());
    }

    public Map<String, ItemStack> getItems() {
        return items;
    }

}
