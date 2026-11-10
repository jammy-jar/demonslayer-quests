package me.jammy.quests.menu;

import mc.obliviate.inventory.Gui;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class JumpscareMenu extends Gui {
    public JumpscareMenu(@NotNull final Player player) {
        super(player, "jumpscare", "", 1);
    }
}
