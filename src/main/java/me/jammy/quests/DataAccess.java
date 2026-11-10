package me.jammy.quests;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import me.jammy.quests.model.progress.LevellableProgress;
import me.jammy.quests.model.progress.Progress;
import me.jammy.quests.model.quest.Quest;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.quest.Quests;
import me.jammy.quests.model.requirement.RequirementClassification;
import me.jammy.quests.model.requirement.RequirementType;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.user.UserBuilder;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataAccess {
    private static final OasisQuests plugin = OasisQuests.instance();
    private static final QuestManager questManager = plugin.getQuestManager();
    private static final Configuration config = plugin.getConfig();
    private static final Logger log = plugin.getLogger();
    private static Connection conn;
    private static final String storageMethod = config.getString("storage-method");

    private static final BiMap<Quests, Integer> questIdMap = HashBiMap.create();

    public static void connect() {
        try {
            if (storageMethod.equalsIgnoreCase("sqlite"))
                connectSQLite();
            else if (storageMethod.equalsIgnoreCase("mysql"))
                connectMySQL();
        } catch (SQLException e) {
            log.warning(e.getMessage());
        }
    }

    public static void connectSQLite() throws SQLException {
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/quests.db";
        conn = DriverManager.getConnection(url);
        log.info("Connection to SQLite has been established!");
    }

    public static void connectMySQL() throws SQLException {
        ConfigurationSection dataConf = config.getConfigurationSection("data");

        String user = dataConf.getString("username");
        String password = dataConf.getString("password");
        String url = "jdbc:mysql://" + dataConf.getString("address") + "/" + dataConf.getString("database");

        conn = DriverManager.getConnection(url, user, password);
        log.info("Connection to SQLite has been established!");
    }

    public static void disconnect() {
        try {
            conn.close();
        } catch (SQLException ignored) {
        }
    }

    public static void initDb() throws SQLException, IOException {
        connect();
        String setup;
        try (InputStream in = plugin.getResource("dbsetup_" + storageMethod.toLowerCase() + ".sql")) {
            assert in != null;
            //noinspection BlockingMethodInNonBlockingContext
            setup = new String(in.readAllBytes());
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not read db setup file.", e);
            throw e;
        }
        String[] queries = setup.split(";");

        for (String query : queries) {
            if (query.isBlank()) continue;
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
        log.info("§2Database setup complete.");
    }

    public static void updateUser(
            UUID uuid,
            Collection<Progress> progresses,
            Set<Quests> activeQuests
    ) {
        for (Quest quest : questManager.quests()) {
            boolean active = activeQuests.contains(quest.getQuest());
            updateQuest(uuid.toString(), quest.getQuest(), active);
        }

        for (Progress progress : progresses)
            updateProgress(uuid.toString(), progress.getRequirement(), progress);
    }

    private static void updateProgress(String uuid, RequirementType requirementType, Progress progress) {
        if (questIdMap.get(requirementType.getQuest()) == null)
            addQuest(requirementType.getQuest());

        Bukkit.getScheduler().runTaskLater(OasisQuests.getInstance(), () -> {
            int playerQuestId;

            try (PreparedStatement stmt = conn.prepareStatement(
                    """
                            SELECT id
                            FROM player_quests
                            WHERE uuid = ? AND quest_id = ?;
                            """
            )) {
                stmt.setString(1, uuid);
                stmt.setInt(2, questIdMap.get(requirementType.getQuest()));
                ResultSet resultSet = stmt.executeQuery();

                if (!resultSet.next())
                    return;
                playerQuestId = resultSet.getInt(1);
            } catch (SQLException e) {
                log.warning("Could not update progress. Error: " + e);
                playerQuestId = -1;
            }

            if (playerQuestId == -1) {
                log.warning("User Quest could not be fetched/ created. User: " + uuid + ", Quest: " + requirementType.getQuest());
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    """
                            SELECT player_quest_id
                            FROM quest_progress
                            WHERE player_quest_id = ? AND progress_type = ?;
                            """
            )) {
                stmt.setInt(1, playerQuestId);
                stmt.setString(2, requirementType.toString());
                ResultSet resultSet = stmt.executeQuery();
                if (!resultSet.next()) {
                    insertProgress(playerQuestId, requirementType, progress);
                    return;
                }
            } catch (SQLException e) {
                log.warning("Could not update progress. Error: " + e);
            }

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE quest_progress SET progress = ? WHERE player_quest_id = ? AND progress_type = ?"
            )) {
                stmt.setInt(1, progress.getAmount());
                stmt.setInt(2, playerQuestId);
                stmt.setString(3, requirementType.toString());
                stmt.execute();
            } catch (SQLException e) {
                log.warning("Could not update progress. Error: " + e);
            }

            if (!(progress instanceof LevellableProgress levellableProgress))
                return;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE quest_level SET lvl = ? WHERE player_quest_id = ?"
            )) {
                stmt.setInt(1, levellableProgress.getLevel());
                stmt.setInt(2, playerQuestId);
                stmt.execute();
            } catch (SQLException e) {
                log.warning("Could not update progress. Error: " + e);
            }
        }, 10L);
    }

    private static int insertQuest(String uuid, Quests quest) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO player_quests (uuid, quest_id, active) VALUES (?, ?, 0)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            stmt.setString(1, uuid);
            stmt.setInt(2, questIdMap.get(quest));
            stmt.execute();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            generatedKeys.next();
            return generatedKeys.getInt(1);
        } catch (SQLException e) {
            log.warning("Could not insert quest. Error: " + e);
            return -1;
        }
    }

    private static void updateQuest(String uuid, Quests quest, boolean active) {
        Bukkit.getScheduler().runTaskAsynchronously(OasisQuests.getInstance(), () -> {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id FROM player_quests WHERE uuid = ? AND quest_id = ?;"
            )) {
                stmt.setString(1, uuid);
                stmt.setInt(2, questIdMap.get(quest));
                ResultSet resultSet = stmt.executeQuery();

                int playerQuestId;
                if (!resultSet.next())
                    playerQuestId = insertQuest(uuid, quest);
                else
                    playerQuestId = resultSet.getInt(1);

                try (PreparedStatement stmt2 = conn.prepareStatement(
                        "UPDATE player_quests SET active = ? WHERE id = ?"
                )) {
                    stmt2.setBoolean(1, active);
                    stmt2.setInt(2, playerQuestId);
                    stmt2.execute();
                }
            } catch (SQLException e) {
                log.warning("Could not update quest. Error: " + e);
            }
        });
    }

    private static void insertProgress(int playerQuestId, RequirementType requirementType, Progress progress) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO quest_progress (player_quest_id, progress, progress_type) VALUES (?, ?, ?)"
        )) {
            stmt.setInt(1, playerQuestId);
            stmt.setInt(2, progress.getAmount());
            stmt.setString(3, requirementType.toString());
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!(progress instanceof LevellableProgress levellableProgress))
            return;
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO quest_level (player_quest_id, lvl) VALUES (?, ?)"
        )) {
            stmt.setInt(1, playerQuestId);
            stmt.setInt(2, levellableProgress.getLevel());
            stmt.execute();
        } catch (SQLException e) {
            log.warning("Could not insert progress. Error: " + e);
        }
    }

    public static void initUserQuests(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(OasisQuests.getInstance(), () -> {
            List<Progress> progresses = Lists.newArrayList();
            Set<Quests> activeQuests = Sets.newHashSet();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
                return;

            try (PreparedStatement stmt = conn.prepareStatement(
                    """
                            SELECT q.id, q.quest_id, q.active, p.progress, p.progress_type, l.lvl
                            FROM player_quests q
                            JOIN quest_progress p ON q.id = p.player_quest_id
                            LEFT JOIN quest_level l ON q.id = l.player_quest_id
                            WHERE q.uuid = ?;
                            """
            )) {
                stmt.setString(1, uuid.toString());
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    int questId = resultSet.getInt("quest_id");
                    Quests quest = questIdMap.inverse().get(questId);
                    RequirementType requirementType = RequirementType.getEnum(resultSet.getString("progress_type"));
                    int progress = resultSet.getInt("progress");
                    if (requirementType.getClassification() == RequirementClassification.LEVELLABLE) {
                        // TODO: Fix level returning 0.
                        int level = resultSet.getInt("lvl");
                        if (level < 1) {
                            log.warning(questIdMap.inverse().get(questId) + " quest Level could not be retrieved for user: " + uuid + ".");
                            level = 1;
                        }
                        progresses.add(new LevellableProgress(
                                requirementType,
                                progress,
                                level
                        ));
                    } else if (requirementType.getClassification() == RequirementClassification.QUANTIFIABLE) {
                        progresses.add(new Progress(
                                requirementType,
                                progress
                        ));
                    }

                    if (resultSet.getBoolean("active"))
                        activeQuests.add(quest);
                }
                if (progresses.isEmpty()) {
                    User user = new UserBuilder(player).build();
                    plugin.getUserManager().addUser(user);
                    plugin.getUserManager().updateUser(user);
                }
            } catch (SQLException e) {
                log.warning("Could not retrieve User. Error: " + e);
                return;
            }

            progresses.forEach(p -> log.info("UUID: " + uuid + ". Quest: " + p.getRequirement().getQuest() + ". Progress Type: " + p.getRequirement() + ". Progress: " + p.getAmount()));
            plugin.getUserManager().addUser(new UserBuilder(player)
                    .progress(progresses)
                    .activeQuests(activeQuests)
                    .build());
        });
    }

    public static void setQuestIdMap() {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, name FROM quest;"
        )) {
            ResultSet results = stmt.executeQuery();
            while (results.next())
                questIdMap.put(Quests.getEnum(results.getString("name")), results.getInt("id"));
        } catch (SQLException e) {
            log.warning("Could not add quest database. Error: " + e);
        }
    }

    public static void addQuest(Quests quest) {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT id FROM quest WHERE name = ?;"
        )) {
            stmt.setString(1, quest.toString());
            ResultSet results = stmt.executeQuery();
            if (results.next())
                return;
        } catch (SQLException e) {
            log.warning("Could not add quest database. Error: " + e);
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO quest (name, type) VALUES (?, ?)"
        )) {
            stmt.setString(1, quest.toString());
            stmt.setString(2, quest.getQuestType().toString());
            stmt.execute();
        } catch (SQLException e) {
            log.warning("Could not add quest to database. Error: " + e);
        }

        setQuestIdMap();
    }

    public static void resetDailys() {
        log.info("Daily Quests are being Reset!");
        try (PreparedStatement stmt = conn.prepareStatement(
                """         
                        UPDATE quest_progress
                        SET progress = 0
                        WHERE player_quest_id IN (
                            SELECT quest_progress.player_quest_id
                            FROM quest_progress
                            JOIN player_quests ON quest_progress.player_quest_id = player_quests.id
                            JOIN quest ON player_quests.quest_id = quest.id
                            WHERE quest.type = 'daily'
                        );
                        """)) {
            stmt.execute();
        } catch (SQLException e) {
            log.warning("Could not reset dailys in database. Error: " + e);
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                """
                        UPDATE player_quests
                        SET active = 0
                        WHERE id IN (
                            SELECT player_quests.id
                            FROM player_quests
                            JOIN quest ON player_quests.quest_id = quest.id
                            WHERE quest.type = 'daily'
                        );
                        """)) {
            stmt.execute();
        } catch (SQLException e) {
            log.warning("Could not reset dailys in database. Error: " + e);
        }
    }
}
