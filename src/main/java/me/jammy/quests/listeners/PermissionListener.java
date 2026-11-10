package me.jammy.quests.listeners;

import me.jammy.quests.OasisQuests;
import me.jammy.quests.model.quest.QuestManager;
import me.jammy.quests.model.user.UserManager;
import me.jammy.quests.model.user.User;
import me.jammy.quests.model.quest.Quests;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

public class PermissionListener {
    private final OasisQuests plugin;
    private final LuckPerms luckPerms;
    private final UserManager userManager;
    private final QuestManager questManager;

    public PermissionListener(OasisQuests plugin) {
        this.plugin = plugin;
        this.luckPerms = OasisQuests.getLuckPerms();
        this.userManager = plugin.getUserManager();
        this.questManager = plugin.getQuestManager();
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();
        eventBus.subscribe(this.plugin, NodeAddEvent.class, this::onNodeAdd);
        eventBus.subscribe(this.plugin, NodeRemoveEvent.class, this::onNodeRemove);
    }

    public void onNodeAdd(NodeAddEvent event) {
        if (!event.isUser())
            return;

        net.luckperms.api.model.user.User target = (net.luckperms.api.model.user.User) event.getTarget();
        Node node = event.getNode();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Player player = this.plugin.getServer().getPlayer(target.getUniqueId());
            if (player == null)
                return; // Player not online.

            if (node instanceof PermissionNode) {
                String permission = ((PermissionNode) node).getPermission();
                if (!permission.startsWith("quests.special."))
                    return;
                User user = userManager.getUser(player);
                if (user == null)
                    return;

                String questName = permission.substring(permission.lastIndexOf(".") + 1);
                user.addSpecial(questManager.getSpecial(Quests.getEnum(questName)));
            }
        });
    }

    public void onNodeRemove(NodeRemoveEvent event) {
        if (!event.isUser())
            return;

        net.luckperms.api.model.user.User target = (net.luckperms.api.model.user.User) event.getTarget();
        Node node = event.getNode();
        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
            Player player = this.plugin.getServer().getPlayer(target.getUniqueId());
            if (player == null)
                return; // Player not online.

            if (node instanceof PermissionNode) {
                String permission = ((PermissionNode) node).getPermission();
                if (!permission.startsWith("quests.special."))
                    return;
                User user = userManager.getUser(player);
                if (user == null)
                    return;

                String questName = permission.substring(permission.lastIndexOf(".") + 1);
                user.removeSpecial(questManager.getSpecial(Quests.getEnum(questName)));
            }
        });
    }
}
