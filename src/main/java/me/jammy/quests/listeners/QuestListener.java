package me.jammy.quests.listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.jammy.quests.OasisQuests;
import me.jammy.quests.conf.NpcVisitsConf;
import me.jammy.quests.conf.UniqueKillsConf;
import me.jammy.quests.event.PlayerOnlineEvent;
import me.jammy.quests.menu.ConfirmMenu;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.campaign.Campaign;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserManager;
import me.vaindev.demonslayer.events.DemonKillEvent;
import me.vaindev.demonslayer.events.NichirinForgeEvent;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class QuestListener implements Listener {

    private static final Set<Material> woodTypes = Sets.newHashSet(
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.MANGROVE_LOG,
            Material.DARK_OAK_LOG, Material.OAK_LOG, Material.JUNGLE_LOG,
            Material.SPRUCE_LOG
    );
    private static final Set<Material> stoneTypes = Sets.newHashSet(
            Material.STONE, Material.BLACKSTONE, Material.COBBLED_DEEPSLATE,
            Material.DEEPSLATE, Material.COBBLESTONE, Material.TERRACOTTA,
            Material.TUFF, Material.DIORITE, Material.ANDESITE
    );
    private static final Set<Material> crops = Sets.newHashSet(
            Material.BEETROOTS, Material.CARROTS, Material.POTATOES,
            Material.WHEAT, Material.COCOA_BEANS, Material.NETHER_WART
    );
    private static final Set<Material> blockCrops = Sets.newHashSet(
            Material.PUMPKIN, Material.MELON, Material.CHORUS_PLANT
    );
    private static final Set<Material> fish = Sets.newHashSet(
            Material.SALMON, Material.COD, Material.PUFFERFISH,
            Material.TROPICAL_FISH
    );

    private final HashMap<String, Player> brewerMap = Maps.newHashMap();
    private final Set<UUID> recentlyInteracted = Sets.newHashSet();

    private final OasisQuests plugin;
    private final CoreProtectAPI coreProtect;
    private final UserManager userManager;
    private final QuestManager questManager;
    private final NpcVisitsConf npcVisitsConf;
    private final UniqueKillsConf uniqueKillsConf;


    public QuestListener(OasisQuests plugin) {
        this.plugin = plugin;
        this.coreProtect = plugin.getCoreProtect();
        this.userManager = plugin.getUserManager();
        this.questManager = plugin.getQuestManager();
        this.npcVisitsConf = plugin.getNpcVisitsConf();
        this.uniqueKillsConf = plugin.getUniqueKillsConf();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material material = event.getBlock().getBlockData().getMaterial();
        Player player = event.getPlayer();
        User user = userManager.getUser(player);
        if (user == null)
            return;

        if (user.inForeignClaim())
            return;
        if (event.isCancelled())
            return;

        // Has this block recently been interacted with?
        if (coreProtect == null || coreProtect.blockLookup(event.getBlock(), 604800).isEmpty()) {
            if (stoneTypes.stream().anyMatch(stone -> stone.equals(material)))
                user.action(RequirementType.MINER);

            if (woodTypes.stream().anyMatch(wood -> wood.equals(material)))
                user.action(RequirementType.WOODCUTTER);

            if (blockCrops.stream().anyMatch(crop -> crop.equals(material)))
                user.action(RequirementType.FARMER);

            user.action(RequirementType.COLLECT_XP_FROM_ORES, event.getExpToDrop());
        }

        if (crops.stream().anyMatch(crop -> crop.equals(material))) {
            if (!(event.getBlock().getBlockData() instanceof Ageable cropBlock))
                return;
            if (cropBlock.getAge() != cropBlock.getMaximumAge())
                return;
            user.action(RequirementType.FARMER);
        }
    }

    @EventHandler
    public void onPlayerOnline(PlayerOnlineEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        user.action(RequirementType.PLAYTIME, event.getMinutes());
        user.action(RequirementType.PLAYTIME_S3, event.getMinutes());
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (CitizensAPI.getNPCRegistry().isNPC(event.getEntity().getKiller()))
            return;
        Entity killer = event.getEntity().getKiller();
        if (killer instanceof Player player) {
            User user = userManager.getUser(player);
            if (user == null)
                return;

            user.action(RequirementType.SLAUGHTERER);

            if (event.getEntity() instanceof Enderman) {
                user.action(RequirementType.KILL_ENDERMEN_MU);
                user.action(RequirementType.KILL_ENDERMEN_KA);
                user.action(RequirementType.KILL_ENDERMEN_GY);
            }

            if (event.getEntity() instanceof Witch)
                user.action(RequirementType.KILL_WITCHES_EN);

            if (event.getEntity() instanceof Creeper)
                user.action(RequirementType.KILL_CREEPERS_EN);

            if (event.getEntity() instanceof MagmaCube) {
                user.action(RequirementType.KILL_MAGMACUBES_KA);
                user.action(RequirementType.KILL_MAGMACUBES_GY);
            }

            if (event.getEntity() instanceof Slime)
                user.action(RequirementType.KILL_SLIMES_GY);

            if (event.getEntity() instanceof Player target) {
                // TODO: Check unique checks work. Check NPC visits work.
                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.KILL_PLAYERS, target.getUniqueId()))
                    user.action(RequirementType.KILL_PLAYERS);
                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.PLAYER_KILL_S3, target.getUniqueId()))
                    user.action(RequirementType.PLAYER_KILL_S3);
                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.PLAYER_KILL_SL, target.getUniqueId()))
                    user.action(RequirementType.PLAYER_KILL_SL);
                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.PLAYER_KILL_AS, target.getUniqueId()))
                    user.action(RequirementType.PLAYER_KILL_AS);

                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.KILL_UNIQUE_PLAYERS_RO, target.getUniqueId()))
                    user.action(RequirementType.KILL_UNIQUE_PLAYERS_RO);
                if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.KILL_UNIQUE_PLAYERS_WA, target.getUniqueId()))
                    user.action(RequirementType.KILL_UNIQUE_PLAYERS_WA);

                if (user.isInWarzone())
                    if (uniqueKillsConf.add(player.getUniqueId(), RequirementType.KILL_PLAYERS_IN_WARZONE, target.getUniqueId()))
                        user.action(RequirementType.KILL_PLAYERS_IN_WARZONE);
            }
        }
    }

    @EventHandler
    public void onDemonDeath(DemonKillEvent event) {
        Player player = event.getKiller();
        User user = userManager.getUser(player);
        if (user == null)
            return;

        if (player.getInventory().getItemInMainHand().getType().isAir())
            user.action(RequirementType.KILL_DEMONS_WITH_FIST);
        else if (player.getInventory().getItemInMainHand().getType() == Material.BOW)
            user.action(RequirementType.KILL_DEMONS_WITH_BOW);

        if (user.isInWarzone()) {
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_MU);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_WA);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_RO);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_EN);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_KA);
            user.action(RequirementType.KILL_DEMONS_IN_WARZONE_GY);
        }

        user.action(RequirementType.KILL_DEMONS);
        user.action(RequirementType.DEMON_KILL_S3);
        user.action(RequirementType.DEMON_KILL_SL);
        user.action(RequirementType.DEMON_KILL_AS);
        user.action(RequirementType.KILL_DEMONS_KY);
        user.action(RequirementType.KILL_DEMONS_RU);
        user.action(RequirementType.KILL_DEMONS_MU);
        user.action(RequirementType.KILL_DEMONS_WA);
        user.action(RequirementType.KILL_DEMONS_RO);
        user.action(RequirementType.KILL_DEMONS_EN);
        user.action(RequirementType.KILL_DEMONS_KA);
        user.action(RequirementType.KILL_DEMONS_GY);
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        Entity breeder = event.getBreeder();

        if (breeder instanceof Player player) {
            User user = userManager.getUser(player);
            if (user == null)
                return;

            user.action(RequirementType.BREEDING);

            if (event.getEntity().getType() == EntityType.CHICKEN)
                user.action(RequirementType.BREED_CHICKENS);
        }
    }

    @EventHandler
    public void onHarvest(PlayerHarvestBlockEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        if (!event.isCancelled())
            user.action(RequirementType.FARMER);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Location location = event.getInventory().getLocation();
        if (event.getClickedInventory() == null)
            return;

        if (location == null || event.getCursor() == null || event.getCurrentItem() == null)
            return;
        String locationString = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();

        if (event.getCursor().getType() == Material.POTION && event.getCurrentItem().getType().isAir() && event.getClickedInventory().getType() == InventoryType.BREWING
                || (event.isShiftClick() && event.getCurrentItem().getType() == Material.POTION && event.getClickedInventory().getType() == InventoryType.PLAYER)) {
            brewerMap.put(locationString, (Player) event.getWhoClicked());
        }

        if (event.getClickedInventory().getType() == InventoryType.BREWING &&
                (event.getCursor().getType().isAir() && event.getCurrentItem().getType() == Material.POTION
                        || event.isShiftClick() && event.getCurrentItem().getType() == Material.POTION)) {
            brewerMap.remove(locationString);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        Location location = event.getBlock().getLocation();
        String locationString = location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();

        Player player = brewerMap.get(locationString);
        if (player == null)
            return;

        List<ItemStack> potions = event.getResults().stream().filter(result -> result.getType() == Material.POTION).toList();

        User user = userManager.getUser(player);
        if (user == null)
            return;

        user.action(RequirementType.ALCHEMIST, potions.size());
        user.action(RequirementType.BREW, potions.size());
        user.action(RequirementType.BREW_AS, potions.size());

        int strengthPotions = getPotionsOfType(potions, PotionType.STRENGTH).size();
        user.action(RequirementType.BREW_STRENGTH_SL, strengthPotions);

        int speedPotions = getPotionsOfType(potions, PotionType.SPEED).size();
        user.action(RequirementType.BREW_SPEED_AM, speedPotions);
    }

    private List<ItemStack> getPotionsOfType(List<ItemStack> potions, PotionType effectType) {
        return potions.stream()
                .filter(potion -> ((PotionMeta) potion.getItemMeta()).getBasePotionData().getType() == effectType)
                .toList();
    }

    @EventHandler
    public void onExperience(PlayerPickupExperienceEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        user.action(RequirementType.EXPERIENCE, event.getExperienceOrb().getExperience());
        user.action(RequirementType.XP_S3, event.getExperienceOrb().getExperience());
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        if (event.getCaught() instanceof Item item) {
            if (fish.stream().anyMatch(f -> item.getItemStack().getType() == f))
                user.action(RequirementType.FISH_FISH);
            user.action(RequirementType.FISHER);
        }
    }

    @EventHandler
    public void onTame(EntityTameEvent event) {
        if (!(event.getOwner() instanceof Player player))
            return;
        User user = userManager.getUser(player);
        if (user == null)
            return;

        user.action(RequirementType.TAMER);
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        User user = userManager.getUser(event.getEnchanter());
        if (user == null)
            return;

        user.action(RequirementType.ENCHANTER);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
            return;

        Player player = event.getPlayer();
        User user = userManager.getUser(player);
        if (user == null)
            return;

        user.action(RequirementType.ADVENTURER);
        user.action(RequirementType.TRAVEL_AS);
        user.action(RequirementType.TRAVEL_AM);

        if (event.getTo().getBlock().getType() == Material.WATER) {
            user.action(RequirementType.SWIM);
        }
    }

    @EventHandler
    public void onNichirinForge(NichirinForgeEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        user.action(RequirementType.NICHIRIN_FORGE_SL);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        User user = userManager.getUser(event.getPlayer());
        if (user == null)
            return;

        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());

        if (recentlyInteracted.contains(user.getPlayer().getUniqueId()))
            return;
        recentlyInteracted.add(user.getPlayer().getUniqueId());
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                () -> recentlyInteracted.remove(user.getPlayer().getUniqueId()), 80);

        if (npc != null) {
            Campaign campaign = questManager.campaigns().stream()
                    .filter(quest -> quest.getNpc() == npc.getId())
                    .findFirst().orElse(null);
            if (campaign != null) {
                new ConfirmMenu(event.getPlayer(), campaign).open();
                if (npcVisitsConf.add(event.getPlayer().getUniqueId(), npc.getId()))
                    user.action(RequirementType.VILLAGE_VISIT_AM);
            }

            questManager.starters().stream()
                    .filter(quest -> quest.getNpc() == npc.getId())
                    .findFirst().ifPresent(starter -> user.action(RequirementType.VISIT_UROKODAKI));
        }
    }
}
